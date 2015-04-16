package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.module.ModuleCandidate;
import jaci.openrio.toast.core.loader.module.ModuleContainer;
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
import java.util.jar.JarFile;
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

    static Logger log = new Logger("Toast|ModuleLoader", Logger.ATTR_DEFAULT);;

    static String[] discoveryDirs;

    public static Pattern classFile = Pattern.compile("([^\\s$]+).class$");
    static URLClassLoader sysLoader = sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    /**
     * Begin loading classes
     */
    public static void init() {
        discoveryDirs = new String[]{new File(ToastBootstrap.toastHome, "modules/").getAbsolutePath(), new File(ToastBootstrap.toastHome, "system/modules/").getAbsolutePath()};
        loadCandidates();
        parseEntries();
        construct();
    }

    /**
     * A List containing a list of class names that are already in the classpath and should be loaded. This is
     * what makes Debug-Simulation possible in Development Environments
     */
    public static ArrayList<String> manualLoadedClasses = new ArrayList<>();

    /**
     * Load a *jar file
     */
    static void sJarFile(File file) throws IOException {
        JarFile jar = new JarFile(file);
        ModuleCandidate container = new ModuleCandidate();
        container.setFile(file);
        for (ZipEntry ze : Collections.list(jar.entries())) {
            if (classFile.matcher(ze.getName()).matches()) {
                container.addClassEntry(ze.getName());
            }
        }
        getCandidates().add(container);
        addURL(file.toURI().toURL());
    }

    /**
     * Look for candidates in the Discovery Directories
     */
    private static void loadCandidates() {
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
            for (String clazz : candidate.getClassEntries()) {
                parseClass(clazz, candidate);
            }
        }

        for (String clazz : manualLoadedClasses) {
            ModuleCandidate candidate = new ModuleCandidate();
            candidate.addClassEntry(clazz);
            parseClass(clazz, candidate);
        }
    }

    static void parseClass(String clazz, ModuleCandidate candidate) {
        try {
            Class c = Class.forName(clazz);
            if (ToastModule.class.isAssignableFrom(c)) {
                ModuleContainer container = new ModuleContainer(c, candidate);
                getContainers().add(container);
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

    /**
     * Prestart all modules
     */
    public static void prestart() {
        for (ModuleContainer container : getContainers())
            container.getModule().prestart();
    }

    /**
     * Start all modules
     */
    public static void start() {
        for (ModuleContainer container : getContainers())
            container.getModule().start();
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