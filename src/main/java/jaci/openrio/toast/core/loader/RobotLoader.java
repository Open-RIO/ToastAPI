package jaci.openrio.toast.core.loader;

import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.io.Storage;
import jaci.openrio.toast.core.loader.annotation.NoLoad;
import jaci.openrio.toast.core.loader.module.ModuleCandidate;
import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ModuleWrapper;
import jaci.openrio.toast.lib.module.ToastModule;
import jaci.openrio.toast.lib.profiler.ProfilerEntity;
import jaci.openrio.toast.lib.profiler.ProfilerSection;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import static jaci.openrio.toast.core.loader.module.ModuleManager.getCandidates;
import static jaci.openrio.toast.core.loader.module.ModuleManager.getContainers;

/**
 * The class responsible for loading modules into Toast. This crawls the Discovery Directories and will also search Manually
 * Loaded Classes, of which are added by Launch Arguments.
 *
 * @author Jaci
 */
public class RobotLoader {

    /* EXTERNALS */
    public static boolean search = false;
    public static ArrayList<String> manualLoadedClasses = new ArrayList<>();
    public static ArrayList<String> coreClasses = new ArrayList<>();
    public static ArrayList<Object> coreObjects = new ArrayList<>();
    public static LinkedList<Method> queuedPrestart = new LinkedList<>();


    /* INTERNALS */
    private static Logger log;
    private static URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    private static boolean isCorePhase = false;
    private static MethodExecutor exec;
    public static Pattern classFile = Pattern.compile("([^\\s$]+).class$");

    /**
     * Also known as the Core pre-init. All initialization is done here.
     */
    public static void preinit(ProfilerSection profiler) {
        log = new Logger("Toast|Loader", Logger.ATTR_DEFAULT);
        if (search)
            loadDevEnvironment(profiler);

        isCorePhase = true;
        loadCandidates(profiler.section("CoreJava"));
        parseCoreEntries(profiler.section("CoreJava"));
    }

    /**
     * Sets up non-core modules for loading
     */
    public static void init(ProfilerSection section) {
        isCorePhase = false;
        ProfilerSection section1 = section.section("Java");
        loadCandidates(section1);
        parseEntries(section1);

        constructModules(section1);
        resolveBranches(section1);
    }

    /* Loaders */

    /**
     * Load a module candidate from a directory. This is usually used in -sim --search.
     */
    static void loadDirectory(File file, ModuleCandidate candidate) throws IOException {
        File[] files = file.listFiles();
        if (files != null)
            for (File f : files)
                loadSubDirectory(file, f, candidate);
    }

    /**
     * Load a subdirectory into a module candidate. This is recursive in order to search for classfiles
     * and extract their path
     */
    static void loadSubDirectory(File main, File dig, ModuleCandidate candidate) {
        if (dig.isDirectory()) {
            File[] files = dig.listFiles();
            if (files != null)
                for (File f : files)
                    loadSubDirectory(main, f, candidate);
        } else if (classFile.matcher(dig.getName()).matches()) {
            Path pathCurrent = Paths.get(dig.getAbsolutePath());
            Path pathMain = Paths.get(main.getAbsolutePath());
            Path relative = pathMain.relativize(pathCurrent);
            candidate.addClassEntry(relative.toString());
        }
    }

    /**
     * Load a Jar File as a ModuleCandidate. This will automatically parse Manifest attributes
     * and other related functions for module discovery.
     * @param file                  The file of the module.jar
     * @param expandClasspath       Should we expand the classpath to this jarfile if it's a valid module?
     * @throws IOException
     */
    static void loadJar(File file, boolean expandClasspath) throws IOException {
        JarFile jar = new JarFile(file);
        ModuleCandidate container = new ModuleCandidate();

        boolean core = false;
        Manifest mf = jar.getManifest();
        if (mf != null) {
            Attributes attr = mf.getMainAttributes();
            if (attr != null) {
                if (attr.getValue("Toast-Core-Plugin-Class") != null && isCorePhase) {
                    String clazz = (String) attr.getValue("Toast-Core-Plugin-Class");
                    container.setCorePlugin(true, clazz);
                    coreClasses.add(clazz);
                    core = true;
                    log.info("Injected Core Plugin: " + file.getName());
                }
                if (attr.getValue("Toast-Plugin-Class") != null) {
                    log.debug("Bypass Module Detected: " + file.getName());
                    String bypassClass = (String) attr.getValue("Toast-Plugin-Class");
                    container.setBypass(true, bypassClass);
                }
                if (attr.getValue("Robot-Class") != null) {
                    log.debug("Wrapping Module Detected: " + file.getName());
                    String wrapperClazz = attr.getValue("Robot-Class");
                    container.setWrapper(true, wrapperClazz);
                }
            }
        }

        if (core && isCorePhase || !core && !isCorePhase) {
            container.setFile(file);
            for (ZipEntry ze : Collections.list(jar.entries())) {
                if (classFile.matcher(ze.getName()).matches()) {
                    container.addClassEntry(ze.getName());
                }
            }

            log.debug("Preliminary Candidate Added: " + file.getName());
            getCandidates().add(container);
            if (expandClasspath)
                addURL(file.toURI().toURL());
        }
    }

    /* Searchers */

    /**
     * Detect any modules that are present in a development environment (-sim --search)
     */
    static void loadDevEnvironment(ProfilerSection section) {
        section.start("DevEnv");
        for (URL url : sysLoader.getURLs()) {
            try {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    ModuleCandidate candidate = new ModuleCandidate();
                    loadDirectory(f, candidate);
                    getCandidates().add(candidate);
                } else if (EnvJars.isLoadable(f)) {
                    loadJar(f, false);
                }
            } catch (Exception e) {
            }
        }
        section.stop("DevEnv");
    }

    /**
     * Search a directory for .jar files to load as modules. Jars will be automatically loaded, however,
     * non .jar files will still be injected into the classpath.
     */
    public static void search(File dir) {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        if (files != null)
            for (File file : files) {
                try {
                    log.debug("Discovering Module Jar: " + file.getName());
                    loadJar(file, true);
                } catch (Exception e) {
                    log.debug("Could not discover module for candidacy: " + file.getName());
                    log.debugException(e);
                }
            }

        File[] otherFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.endsWith(".jar");
            }
        });

        if (otherFiles != null)
            for (File file : otherFiles) {
                try {
                    log.warn("Non-Jar Module in modules/! This should be in libs/! " + file.getName());
                    addURL(file.toURI().toURL());
                } catch (Exception e) {
                    log.debug("Could not expand classpath for Library: " + file.getName());
                    log.debugException(e);
                }
            }
    }

    /**
     * Similar method to {@link #search(File)}, but will not search for Toast files, but will instead
     * just add it to the load path. This is for libraries that don't load Toast, like Apache Commons,
     * Language Libraries or others.
     */
    public static void search_libs(File dir) {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        if (files != null)
            for (File file : files) {
                try {
                    log.debug("Expanding Classpath for Library: " + file.getName());
                    addURL(file.toURI().toURL());
                } catch (Exception e) {
                    log.debug("Could not expand classpath for Library: " + file.getName());
                    log.debugException(e);
                }
            }

        File[] otherFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.endsWith(".jar");
            }
        });

        if (otherFiles != null)
            for (File file : otherFiles) {
                try {
                    log.debug("Expanding Classpath for Non-Jar Library: " + file.getName());
                    addURL(file.toURI().toURL());
                } catch (Exception e) {
                    log.debug("Could not expand classpath for Non-Jar Library: " + file.getName());
                    log.debugException(e);
                }
            }
    }

    /* Candidation */

    /**
     * Start discovery for Module Candidates.
     */
    private static void loadCandidates(ProfilerSection section) {
        section.start("Candidate");
        File[] search_dirs = new File[] { new File(ToastBootstrap.toastHome, "modules/") };
        File[] lib_dirs = new File[0];
        if (!isCorePhase) {
            search_dirs = Storage.USB_Module("modules");
            lib_dirs = Storage.USB_Module("libs");
        }
        for (File dir : lib_dirs) {
            dir.mkdirs();
            search_libs(dir);
        }
        for (File dir : search_dirs) {
            dir.mkdirs();
            search(dir);
        }
        section.stop("Candidate");
    }

    /**
     * Parse the core module candidates and attempt to load them into preinit
     */
    private static void parseCoreEntries(ProfilerSection section) {
        section.start("Parse");
        for (String clazz : coreClasses) {
            try {
                log.debug("Parsing Core Entry: " + clazz);
                Class c = Class.forName(clazz);
                Object object = c.newInstance();
                coreObjects.add(object);
                c.getDeclaredMethod("preinit").invoke(object);
            } catch (Throwable e) {
                log.debug("Could not parse core entry: " + clazz);
                log.debugException(e);
            }
        }
        section.stop("Parse");
    }

    /**
     * Returns false if the NoLoad annotation is present, which stops 'api' classes being loaded.
     */
    static boolean classLoadable(Class clazz) {
        return !clazz.isAnnotationPresent(NoLoad.class);
    }

    /**
     * Attempt to load this class as a ToastModule into the container
     */
    static void parseClass(String clazz, ModuleCandidate candidate) {
        try {
            Class c = Class.forName(clazz);
            if (ToastModule.class.isAssignableFrom(c) && classLoadable(c)) {
                log.debug("Toast Module Class Found: " + clazz + " for candidate: " + candidate.getModuleFile().getName());
                ModuleContainer container = new ModuleContainer(c, candidate);
                getContainers().add(container);
            }
        } catch (Throwable e) {
            log.debug("Could not parse module class: " + clazz);
            log.debugException(e);
        }
    }

    /**
     * Attempt to load this class as a ModuleWrapper into a container
     */
    static void parseWrapper(String clazz, ModuleCandidate candidate) {
        try {
            Class c = Class.forName(clazz);
            if (RobotBase.class.isAssignableFrom(c) && classLoadable(c) && !Toast.class.isAssignableFrom(c)) {
                log.debug("WPILib Class Found: " + clazz + " for candidate: " + candidate.getModuleFile().getName() + "... wrapping...");
                ModuleWrapper wrapper = new ModuleWrapper(candidate.getModuleFile(), c, candidate);
                getContainers().add(new ModuleContainer(wrapper, candidate));
            }
        } catch (Throwable e) {
            log.debug("Could not parse wrapper class: " + clazz);
            log.debugException(e);
        }
    }

    /**
     * Parse non-core module candidates into their respective containers.
     */
    private static void parseEntries(ProfilerSection section) {
        section.start("Parse");
        for (ModuleCandidate candidate : getCandidates()) {
            if (candidate.isBypass()) {
                parseClass(candidate.getBypassClass(), candidate);
            } else if (candidate.isWrapper()) {
                parseWrapper(candidate.getWrapperClass(), candidate);
            } else
                for (String clazz : candidate.getClassEntries()) {
                    parseClass(clazz, candidate);
                }
            candidate.freeMemory();
        }

        for (String clazz : manualLoadedClasses) {
            log.debug("Manually Loading Class: " + clazz);
            ModuleCandidate candidate = new ModuleCandidate();
            candidate.addClassEntry(clazz);
            parseClass(clazz, candidate);
        }
        section.stop("Parse");
    }

    /* Construction */

    /**
     * Construct all the candidate modules. This will start parsing the names and versions of all the
     * module container main classes, thus setting them up ready for prestart and start.
     */
    private static void constructModules(ProfilerSection section) {
        for (ModuleContainer container : getContainers()) {
            try {
                log.debug("Constructing Module Container: " + container.getCandidate().getModuleFile().getName());
                ProfilerEntity entity = new ProfilerEntity().start();
                container.construct();
                entity.stop();
                entity.setName("Construct");
                section.section("Module").section(container.getName()).pushEntity(entity);
                log.info("Module Loaded: " + container.getDetails());
            } catch (Exception e) {
                log.debug("Could not Construct Module: " + container.getCandidate().getModuleFile().getName());
                log.debugException(e);
            }
        }
    }

    /**
     * Resolve any dependency branches on the ToastModule class. This will load classes if modules or
     * class definitions are present in the Toast environment, acting as a soft dependency
     * {@link jaci.openrio.toast.core.loader.annotation.Branch}
     */
    private static void resolveBranches(ProfilerSection section) {
        ProfilerSection section1 = section.section("Dependency");
        for (ModuleContainer container : getContainers()) {
            log.debug("Resolving Dependencies for Module: " + container.getName());
            section1.start(container.getName());
            container.resolve_branches();
            section1.stop(container.getName());
        }
    }

    /* Callers */

    /**
     * Initialize core modules by calling their init() method.
     */
    public static void initCore(ProfilerSection section) {
        ProfilerSection section1 = section.section("CoreJava");
        section1.start("Init");
        for (Object core : coreObjects) {
            try {
                core.getClass().getDeclaredMethod("init").invoke(core);
            } catch (Throwable e) {
            }
        }
        section1.stop("Init");
    }

    /**
     * Post initialize core modules by calling their postinit() method.
     */
    public static void postCore(ProfilerSection section) {
        ProfilerSection section1 = section.section("CoreJava");
        section1.start("Post");
        for (Object core : coreObjects) {
            try {
                core.getClass().getDeclaredMethod("postinit").invoke(core);
            } catch (Throwable e) {
            }
        }
        section1.stop("Post");
    }

    /**
     * Prestart modules in order of their {@link jaci.openrio.toast.core.loader.annotation.Priority} annotations
     * (assuming the annotation is present, else treat as normal). Queued prestart methods are also invoked (dependencies)
     */
    public static void prestart(ProfilerSection section) throws InvocationTargetException {
        dispatch("prestart", section);
        for (Method method : queuedPrestart) {
            try {
                method.invoke(null);
            } catch (Exception e) {
                Toast.log().error("Error invoking queued method: " + method.getName());
                Toast.log().exception(e);
            }
        }
        queuedPrestart.clear();
    }

    /**
     * Start modules in order of their {@link jaci.openrio.toast.core.loader.annotation.Priority} annotations
     * (assuming the annotation is present, else treat as normal).
     */
    public static void start(ProfilerSection section) throws InvocationTargetException {
        dispatch("start", section);
    }

    /* Util */

    /**
     * Dispatch a method to all containers
     */
    public static void dispatch(String method, ProfilerSection section) throws InvocationTargetException {
        if (exec == null) {
            ToastModule[] mods = new ToastModule[getContainers().size()];
            for (int i = 0; i < mods.length; i++) {
                mods[i] = getContainers().get(i).getModule();
            }
            exec = new MethodExecutor(mods);
        }
        exec.profile(section);
        exec.call(method);
    }

    /**
     * Expand the classpath to include the provided URL
     */
    public static void addURL(URL u) throws IOException {
        Class sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysLoader, u);
        } catch (Throwable t) { }
    }

    /**
     * Returns true if the provided string is a valid class name and a definition is present in the classpath.
     */
    public static boolean classExists(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}