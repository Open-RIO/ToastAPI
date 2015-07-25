package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.annotation.NoLoad;
import jaci.openrio.toast.core.loader.module.ModuleCandidate;
import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.core.thread.ToastThreadPool;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ToastModule;
import jaci.openrio.toast.lib.profiler.Profiler;

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

    /* Disclaimer: The documentation of this code is better described in the Whitepaper. The documentation present
        is hard to justify due to the difficult nature of Classpath manipulation and Loading of external files.
     */

    static Logger log;

    static String[] discoveryDirs;

    public static Pattern classFile = Pattern.compile("([^\\s$]+).class$");
    static URLClassLoader sysLoader = sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    static boolean threaded;
    static ToastThreadPool pool;

    static boolean coreLoading = false;
    public static boolean search = false;

    /**
     * Initialize the Loader. This starts the loading of regular, non-core modules.
     */
    public static void init() {
        try {
            threaded = ToastConfiguration.Property.THREADED_LOADING.asBoolean();
            if (threaded) pool = new ToastThreadPool("Module-Worker");
        } catch (Exception e) {
        }

        loadCandidates();
        parseEntries();

        if (!threaded)
            construct();
        else {
            pool.finish();
            pool.waitForCompletion();
        }

        branches();
    }

    /**
     * Preinit the Loader. This loads the Core Modules and prepares the other modules
     * for loading and candidacy.
     */
    public static void preinit() {
        log = new Logger("Toast|ModuleLoader", Logger.ATTR_DEFAULT);
        if (search) {
            loadDevEnv();
        }
        discoveryDirs = new String[]{new File(ToastBootstrap.toastHome, "modules/").getAbsolutePath(), new File(ToastBootstrap.toastHome, "system/modules/").getAbsolutePath()};
        loadCoreCandidates();
        parseCoreEntries();
    }

    /**
     * A List containing a list of class names that are already in the classpath and should be loaded. This is
     * what makes Debug-Simulation possible in Development Environments
     */
    public static ArrayList<String> manualLoadedClasses = new ArrayList<>();

    public static ArrayList<String> coreClasses = new ArrayList<>();
    public static ArrayList<Object> coreObjects = new ArrayList<>();
    public static LinkedList<Method> queuedPrestart = new LinkedList<>();

    /**
     * Load the module in a development environment if the --search flag is passed to the command line. This ignores
     * everything in the {@link EnvJars} class, but will search for candidates in the output directory.
     */
    static void loadDevEnv() {
        Profiler.INSTANCE.section("Module").start("Dev");
        for (URL url : sysLoader.getURLs()) {
            try {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    ModuleCandidate candidate = new ModuleCandidate();
                    sDirectory(f, candidate);
                    getCandidates().add(candidate);
                } else if (EnvJars.isLoadable(f)) {
                    sJarFile(f, false);
                }
            } catch (Exception e) {
            }
        }
        Profiler.INSTANCE.section("Module").stop("Dev");
    }

    /**
     * Search a directory for class entries with the given candidate
     */
    static void sDirectory(File file, ModuleCandidate candidate) throws IOException {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                sSubDirectory(file, f, candidate);
            }
        }
    }

    /**
     * Search the new subdirectory for the given candidate. This searches for the
     * .class files and will try to get their class name.
     */
    static void sSubDirectory(File main, File dig, ModuleCandidate candidate) {
        if (dig.isDirectory()) {
            File[] files = dig.listFiles();
            if (files != null)
                for (File f : files) {
                    sSubDirectory(main, f, candidate);
                }
        } else if (classFile.matcher(dig.getName()).matches()) {
            Path pathCurrent = Paths.get(dig.getAbsolutePath());
            Path pathMain = Paths.get(main.getAbsolutePath());
            Path relative = pathMain.relativize(pathCurrent);
            candidate.addClassEntry(relative.toString());
        }
    }

    /**
     * Load a *jar file and search the manifest for details.
     */
    static void sJarFile(File file, boolean newDep) throws IOException {
        JarFile jar = new JarFile(file);
        ModuleCandidate container = new ModuleCandidate();

        boolean core = false;
        Manifest mf = jar.getManifest();
        if (mf != null) {
            Attributes attr = mf.getMainAttributes();
            if (attr != null) {
                if (attr.getValue("Toast-Core-Plugin-Class") != null && coreLoading) {
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

        if (core && coreLoading || !core && !coreLoading) {
            container.setFile(file);
            for (ZipEntry ze : Collections.list(jar.entries())) {
                if (classFile.matcher(ze.getName()).matches()) {
                    container.addClassEntry(ze.getName());
                }
            }

            getCandidates().add(container);
            if (newDep)
                addURL(file.toURI().toURL());
        }
    }

    /**
     * Load CorePlugins from the Discovery Directories.
     */
    private static void loadCoreCandidates() {
        coreLoading = true;
        Profiler.INSTANCE.section("Module").section("CoreJava").start("Candidate");
        for (String currentDirectory : discoveryDirs) {
            File dir = new File(currentDirectory);
            dir.mkdirs();
            search(dir);
        }
        Profiler.INSTANCE.section("Module").section("CoreJava").stop("Candidate");
    }

    /**
     * Look for candidates in the Discovery Directories
     */
    private static void loadCandidates() {
        coreLoading = false;
        Profiler.INSTANCE.section("Module").section("Java").start("Candidate");
        boolean usb_override = USBMassStorage.overridingModules();
        if (!usb_override)
            for (String currentDirectory : discoveryDirs) {
                File dir = new File(currentDirectory);
                dir.mkdirs();
                search(dir);
            }

        for (MassStorageDevice device : USBMassStorage.connectedDevices) {
            if (usb_override || device.concurrent_modules) {
                File modulesDir = new File(device.toast_directory, "modules");
                modulesDir.mkdirs();
                search(modulesDir);
            }
        }
        Profiler.INSTANCE.section("Module").section("Java").stop("Candidate");
    }

    /**
     * Recursively search for modules in the given directory
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
                    sJarFile(file, true);
                } catch (Exception e) {
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
                    addURL(file.toURI().toURL());
                } catch (Exception e) {
                }
            }
    }

    /**
     * Parse the candidates to find their ToastModule classes
     */
    private static void parseEntries() {
        Profiler.INSTANCE.section("Module").section("Java").start("Parse");
        for (ModuleCandidate candidate : getCandidates()) {
            if (candidate.isBypass()) {
                handle(new Runnable() {
                    @Override
                    public void run() {
                        parseClass(candidate.getBypassClass(), candidate);
                    }
                });
            } else
                for (String clazz : candidate.getClassEntries()) {
                    handle(new Runnable() {
                        @Override
                        public void run() {
                            parseClass(clazz, candidate);
                        }
                    });
                }
            candidate.freeMemory();
        }

        for (String clazz : manualLoadedClasses) {
            ModuleCandidate candidate = new ModuleCandidate();
            candidate.addClassEntry(clazz);
            handle(new Runnable() {
                @Override
                public void run() {
                    parseClass(clazz, candidate);
                }
            });
        }
        Profiler.INSTANCE.section("Module").section("Java").stop("Parse");
    }

    /**
     * Load CorePlugin classes and instantiate them.
     */
    private static void parseCoreEntries() {
        Profiler.INSTANCE.section("Module").section("CoreJava").start("Parse");
        for (String clazz : coreClasses) {
            try {
                Class c = Class.forName(clazz);
                Object object = c.newInstance();
                coreObjects.add(object);
                c.getDeclaredMethod("preinit").invoke(object);
            } catch (Throwable e) {
            }
        }
        Profiler.INSTANCE.section("Module").section("CoreJava").stop("Parse");
    }

    /**
     * Run the init() method on all the Core Modules that have been loaded
     */
    public static void initCore() {
        for (Object core : coreObjects) {
            try {
                core.getClass().getDeclaredMethod("init").invoke(core);
            } catch (Throwable e) {
            }
        }
    }

    /**
     * Run the postinit() method on all the Core Modules that have been loaded.
     */
    public static void postCore() {
        for (Object core : coreObjects) {
            try {
                core.getClass().getDeclaredMethod("postinit").invoke(core);
            } catch (Throwable e) {
            }
        }
    }

    /**
     * Parse a class name for Module Candidacy, and if it is valid, create a ModuleContainer
     * for it and register it
     */
    static void parseClass(String clazz, ModuleCandidate candidate) {
        try {
            Class c = Class.forName(clazz);
            if (ToastModule.class.isAssignableFrom(c) && classLoadable(c)) {
                ModuleContainer container = new ModuleContainer(c, candidate);
                getContainers().add(container);
                if (threaded) {
                    container.construct();
                    log.info("Module Loaded: " + container.getDetails());
                }
            }
        } catch (Throwable e) {
        }
    }

    /**
     * Checks if the class is a default class created by Toast. If it is, ignore it as it serves no purpose.
     */
    static boolean classLoadable(Class clazz) {
        return !clazz.isAnnotationPresent(NoLoad.class);
    }

    /**
     * Construct all the modules
     */
    private static void construct() {
        Profiler.INSTANCE.section("Module").section("Java").start("Construction");
        for (ModuleContainer container : getContainers()) {
            try {
                container.construct();
                log.info("Module Loaded: " + container.getDetails());
            } catch (Exception e) {
            }
        }
        Profiler.INSTANCE.section("Module").section("Java").stop("Construction");
    }

    /**
     * Resolves all the branches for each container
     */
    private static void branches() {
        for (ModuleContainer container : getContainers()) {
            Profiler.INSTANCE.section("Module").section("Java").section("Dependency").start(container.getName());
            container.resolve_branches();
            Profiler.INSTANCE.section("Module").section("Java").section("Dependency").stop(container.getName());
        }
    }

    /**
     * Handle a runnable. If we're threaded, add it to the ThreadPool, else, execute it now
     */
    private static void handle(Runnable r) {
        if (threaded)
            pool.addWorker(r);
        else
            r.run();
    }

    /**
     * Returns true if a class exists in the class path with the given name. This avoids the ClassNotFoundException
     * you may get from Class.forName
     */
    public static boolean classExists(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Prestart all modules that have been loaded.
     */
    public static void prestart() {
//        for (ModuleContainer container : getContainers())
//            container.getModule().prestart();
        dispatch("prestart");
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
    public static void start() {
        dispatch("start");
    }

    static MethodExecutor exec;

    /**
     * Dispatch a method to the MethodExecutor
     */
    public static void dispatch(String method) {
        if (exec == null) {
            ToastModule[] mods = new ToastModule[getContainers().size()];
            for (int i = 0; i < mods.length; i++) {
                mods[i] = getContainers().get(i).getModule();
            }
            exec = new MethodExecutor(mods);
        }
        exec.profile(Profiler.INSTANCE.section("Module").section("Java").section(method));
        exec.call(method);
    }

    /**
     * Add a URL to the System Class Loader
     */
    public static void addURL(URL u) throws IOException {
        Class sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysLoader, u);
        } catch (Throwable t) {
        }

    }

}