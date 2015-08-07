package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.core.io.Storage;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.annotation.NoLoad;
import jaci.openrio.toast.core.loader.module.ModuleCandidate;
import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.core.thread.ToastThreadPool;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ToastModule;
import jaci.openrio.toast.lib.profiler.Profiler;
import jaci.openrio.toast.lib.profiler.ProfilerEntity;
import jaci.openrio.toast.lib.profiler.ProfilerSection;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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

    static void loadDirectory(File file, ModuleCandidate candidate) throws IOException {
        File[] files = file.listFiles();
        if (files != null)
            for (File f : files)
                loadSubDirectory(file, f, candidate);
    }

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
                    String bypassClass = (String) attr.getValue("Toast-Plugin-Class");
                    container.setBypass(true, bypassClass);
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

            getCandidates().add(container);
            if (expandClasspath)
                addURL(file.toURI().toURL());
        }
    }

    /* Searchers */

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
                    loadJar(file, true);
                } catch (Exception e) { }
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
                    addURL(file.toURI().toURL());
                } catch (Exception e) { }
            }
    }

    /* Candidation */

    private static void loadCandidates(ProfilerSection section) {
        section.start("Candidate");
        File[] search_dirs = new File[] { new File(ToastBootstrap.toastHome, "modules/") };
        if (!isCorePhase)
            search_dirs = Storage.USB_Module("modules");
        for (File dir : search_dirs) {
            dir.mkdirs();
            search(dir);
        }
        section.stop("Candidate");
    }

    private static void parseCoreEntries(ProfilerSection section) {
        section.start("Parse");
        for (String clazz : coreClasses) {
            try {
                Class c = Class.forName(clazz);
                Object object = c.newInstance();
                coreObjects.add(object);
                c.getDeclaredMethod("preinit").invoke(object);
            } catch (Throwable e) {
            }
        }
        section.stop("Parse");
    }

    static boolean classLoadable(Class clazz) {
        return !clazz.isAnnotationPresent(NoLoad.class);
    }

    static void parseClass(String clazz, ModuleCandidate candidate) {
        try {
            Class c = Class.forName(clazz);
            if (ToastModule.class.isAssignableFrom(c) && classLoadable(c)) {
                ModuleContainer container = new ModuleContainer(c, candidate);
                getContainers().add(container);
            }
        } catch (Throwable e) {
        }
    }

    private static void parseEntries(ProfilerSection section) {
        section.start("Parse");
        for (ModuleCandidate candidate : getCandidates()) {
            if (candidate.isBypass()) {
                parseClass(candidate.getBypassClass(), candidate);
            } else
                for (String clazz : candidate.getClassEntries()) {
                    parseClass(clazz, candidate);
                }
            candidate.freeMemory();
        }

        for (String clazz : manualLoadedClasses) {
            ModuleCandidate candidate = new ModuleCandidate();
            candidate.addClassEntry(clazz);
            parseClass(clazz, candidate);
        }
        section.stop("Parse");
    }

    /* Construction */

    private static void constructModules(ProfilerSection section) {
        for (ModuleContainer container : getContainers()) {
            try {
                ProfilerEntity entity = new ProfilerEntity().start();
                container.construct();
                entity.stop();
                entity.setName("Construct");
                section.section("Module").section(container.getName()).pushEntity(entity);
                log.info("Module Loaded: " + container.getDetails());
            } catch (Exception e) {
            }
        }
    }

    private static void resolveBranches(ProfilerSection section) {
        ProfilerSection section1 = section.section("Dependency");
        for (ModuleContainer container : getContainers()) {
            section1.start(container.getName());
            container.resolve_branches();
            section1.stop(container.getName());
        }
    }

    /* Callers */

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


    public static void prestart(ProfilerSection section) {
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
     * Start all modules that have been loaded.
     */
    public static void start(ProfilerSection section) {
        dispatch("start", section);
    }

    /* Util */

    public static void dispatch(String method, ProfilerSection section) {
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

    public static void addURL(URL u) throws IOException {
        Class sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysLoader, u);
        } catch (Throwable t) { }
    }

    public static boolean classExists(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}