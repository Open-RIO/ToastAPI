package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.module.ModuleCandidate;
import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.core.thread.ToastThreadPool;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ToastModule;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
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

    static Logger log = new Logger("Toast|ModuleLoader", Logger.ATTR_DEFAULT);
    ;

    static String[] discoveryDirs;

    public static Pattern classFile = Pattern.compile("([^\\s$]+).class$");
    static URLClassLoader sysLoader = sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    static boolean threaded;
    static ToastThreadPool pool;

    static boolean coreLoading = false;

    /**
     * Begin loading classes
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

    public static void preinit() {
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

    /**
     * Load a *jar file
     */
    static void sJarFile(File file) throws IOException {
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
            addURL(file.toURI().toURL());
        }
    }

    /**
     * Load CorePlugins from the Discovery Directories.
     */
    private static void loadCoreCandidates() {
        coreLoading = true;
        for (String currentDirectory : discoveryDirs) {
            File dir = new File(currentDirectory);
            dir.mkdirs();
            search(dir);
        }
    }

    /**
     * Look for candidates in the Discovery Directories
     */
    private static void loadCandidates() {
        coreLoading = false;
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
    }

    /**
     * Search for modules in the given directory
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
                    sJarFile(file);
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
    }

    /**
     * Load CorePlugin classes and instantiate them.
     */
    private static void parseCoreEntries() {
        for (String clazz : coreClasses) {
            try {
                Class c = Class.forName(clazz);
                Object object = c.newInstance();
                coreObjects.add(object);
                c.getDeclaredMethod("preinit").invoke(object);
            } catch (Throwable e) {
            }
        }
    }

    public static void initCore() {
        for (Object core : coreObjects) {
            try {
                core.getClass().getDeclaredMethod("init").invoke(core);
            } catch (Throwable e) {
            }
        }
    }

    static void parseClass(String clazz, ModuleCandidate candidate) {
        try {
            Class c = Class.forName(clazz);
            if (ToastModule.class.isAssignableFrom(c)) {
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
     * Construct all the modules
     */
    private static void construct() {
        for (ModuleContainer container : getContainers()) {
            try {
                container.construct();
                log.info("Module Loaded: " + container.getDetails());
            } catch (Exception e) {
            }
        }
    }

    private static void branches() {
        for (ModuleContainer container : getContainers()) {
            container.resolve_branches();
        }
    }

    private static void handle(Runnable r) {
        if (threaded)
            pool.addWorker(r);
        else
            r.run();
    }

    public static boolean classExists(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Prestart all modules
     */
    public static void prestart() {
//        for (ModuleContainer container : getContainers())
//            container.getModule().prestart();
        dispatch("prestart");
    }

    /**
     * Start all modules
     */
    public static void start() {
        dispatch("start");
    }

    static MethodExecutor exec;

    public static void dispatch(String method) {
        if (exec == null) {
            ToastModule[] mods = new ToastModule[getContainers().size()];
            for (int i = 0; i < mods.length; i++) {
                mods[i] = getContainers().get(i).getModule();
            }
            exec = new MethodExecutor(mods);
        }
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