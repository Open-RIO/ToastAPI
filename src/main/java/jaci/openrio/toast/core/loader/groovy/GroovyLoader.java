package jaci.openrio.toast.core.loader.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.GroovyScript;
import jaci.openrio.toast.lib.state.RobotState;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * A Loader for loading classes beneath the toast/groovy/ tree. This allows for
 * Groovy Scripts to be executed as if they were modules
 *
 * @author Jaci
 */
public class GroovyLoader {

    /* Disclaimer: The documentation of this code is better described in the Whitepaper. The documentation present
        is hard to justify due to the difficult nature of Classpath manipulation and Loading of external files.
     */

    static String searchDir = "groovy";
    static Logger logger = new Logger("Toast|GroovyLoader", Logger.ATTR_DEFAULT);
    static ClassLoader loader = ClassLoader.getSystemClassLoader();
    static GroovyClassLoader gLoader = new GroovyClassLoader(loader);
    public static Pattern groovyFile = Pattern.compile("([^\\s$]+).groovy$");

    public static Vector<GroovyScript> scripts = new Vector<>();
    public static HashMap<File, GroovyObject> groovyFiles = new HashMap<>();
    public static HashMap<String, GroovyObject> groovyObjects = new HashMap<>();

    public static ArrayList<File> customFiles = new ArrayList<>();
    public static ArrayList<String> customClasses = new ArrayList<>();

    static boolean loadingCore = false;
    public static boolean coreScriptsLoaded = false;

    /**
     * Initialize the Loader. This starts the loading of regular, non-core scripts.
     */
    public static void init() {
        loadingCore = false;
        loadScripts();
    }

    /**
     * Preinit the Loader. This loads the CoreScripts and prepares the other scripts
     * for loading and candidacy.
     */
    public static void preinit() {
        loadingCore = true;
        loadScripts();
    }

    /**
     * Return the GroovyClassLoader used to load Groovy Scripts. This is based off the System
     * Class Loader.
     */
    public static GroovyClassLoader getGLoader() {
        return gLoader;
    }

    /**
     * Load the scripts. This is a common method for both Core and Non-Core scripts.
     * This method works by searching the ToastHome for Groovy Scripts. This also loads
     * scripts that have been manually added by the User through Runtime arguments.
     */
    static void loadScripts() {
        try {
            File search = new File(ToastBootstrap.toastHome, searchDir);
            search.mkdirs();
            search(search);

            for (File custom : customFiles)
                if (custom.isDirectory()) {
                    search(custom);
                } else loadFile(custom);

            for (String name : customClasses)
                loadClassName(name);
        } catch (Exception e) {
            logger.error("Could not load Groovy Scripts: ");
            logger.exception(e);
        }
    }

    /**
     * Recursively search a directory for Groovy Scripts to load. This works on the premise
     * of File Extensions, matching .groovy for regular scripts and .corescript for scripts
     * to be loaded at Core Runtime. These are passed to the {@link #loadFiles(File[])} method
     * to be dealt with.
     */
    public static void search(File file) {
        File[] groovy = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (!loadingCore)
                    return name.endsWith(".groovy");
                else
                    return name.endsWith(".corescript");
            }
        });
        loadFiles(groovy);

        File[] subdirectory = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        for (File f : subdirectory)
            search(f);
    }

    /**
     * Recursively load all files in the given File array. This passes to the {@link #loadFile(File)} method to be
     * parsed and registered on the ClassPath.
     */
    public static void loadFiles(File[] files) {
        if (files != null)
            for (File file : files) {
                try {
                    GroovyObject object = loadFile(file);
                    if (loadingCore)
                        coreScriptsLoaded = true;
                } catch (Exception e) {
                    logger.error("Could not load Groovy Script: " + file.getName());
                    logger.exception(e);
                }
            }
    }

    public static GroovyObject loadClassName(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class groovyClass = loader.loadClass(name);
        GroovyObject object = (GroovyObject) groovyClass.newInstance();

        if (object instanceof GroovyScript) {
            GroovyScript script = (GroovyScript) object;
            script.loadScript();
            scripts.add(script);
        }
        groovyObjects.put(object.getClass().getName(), object);

        try {
            object.invokeMethod("init", null);
        } catch (Exception e) {}
        return object;
    }

    /**
     * Link to {@link #loadFile(File, boolean)}, with the 'register' boolean argument defaulting to 'true'
     */
    public static GroovyObject loadFile(File file) throws IOException, IllegalAccessException, InstantiationException {
        return loadFile(file, true);
    }

    /**
     * Load a single file into the classpath and parse the groovy file. This method will register the file on the groovyFiles and groovyObjects
     * lists if required, as well as instantiate it if it is a subclass of {@link GroovyScript}. The init() static method is called when this
     * method occurs.
     * @param file      The File to load. This should point to a .groovy or .corescript file.
     * @param register  Should we register this? This is true for scripts, but false for things like Autorun and Preferences files.
     */
    public static GroovyObject loadFile(File file, boolean register) throws IOException, IllegalAccessException, InstantiationException {
        Class groovyClass = gLoader.parseClass(file);
        GroovyObject object = (GroovyObject) groovyClass.newInstance();

        if (object instanceof GroovyScript) {
            GroovyScript script = (GroovyScript) object;
            script.loadScript();
            scripts.add(script);
        }
        if (register) {
            groovyFiles.put(file, object);
            groovyObjects.put(object.getClass().getName(), object);
        }

        try {
            object.invokeMethod("init", new Object[0]);
        } catch (Exception e) {}
        return object;
    }

    /**
     * Call prestartRobot() on all the objects registered.
     */
    public static void prestart() {
        for (GroovyObject object : groovyObjects.values()) {
            try {
                object.invokeMethod("prestartRobot", new Object[0]);
            } catch (Exception e) {}
        }
    }

    /**
     * Call startRobot() on all the objects registered.
     */
    public static void start() {
        for (GroovyObject object : groovyObjects.values()) {
            try {
                object.invokeMethod("startRobot", new Object[0]);
            } catch (Exception e) {}
        }
    }

    /**
     * Call tickState() on all the objects registered.
     */
    public static void tick(RobotState state) {
        for (GroovyObject object : groovyObjects.values()) {
            try {
                object.invokeMethod("tickState", state);
            } catch (Exception e) {}
        }
    }

    /**
     * Call transitionState() on all the objects registered.
     */
    public static void transition(RobotState state) {
        for (GroovyObject object : groovyObjects.values()) {
            try {
                object.invokeMethod("transitionState", state);
            } catch (Exception e) {}
        }
    }

    /**
     * Get an Object with the Given Class or Filename that has been registered.
     */
    public static GroovyObject getObject(String name) {
        return groovyObjects.get(name);
    }

}
