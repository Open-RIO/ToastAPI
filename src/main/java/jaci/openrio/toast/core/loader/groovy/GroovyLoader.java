package jaci.openrio.toast.core.loader.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.GroovyScript;
import org.codehaus.groovy.tools.GroovyClass;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Loader for loading classes beneath the toast/groovy/ tree. This allows for
 * Groovy Scripts to be executed as if they were modules
 *
 * @author Jaci
 */
public class GroovyLoader {

    static String searchDir = "groovy";
    static Logger logger;
    static ClassLoader loader;
    static GroovyClassLoader gLoader;

    static ArrayList<GroovyScript> scripts;
    public static HashMap<File, GroovyObject> groovyFiles;

    public static void init() {
        logger = new Logger("Toast|GroovyLoader", Logger.ATTR_DEFAULT);
        loader = ClassLoader.getSystemClassLoader();
        scripts = new ArrayList<>();
        groovyFiles = new HashMap<>();

        loadScripts();
    }

    static void loadScripts() {
        try {
            gLoader = new GroovyClassLoader(loader);
            File search = new File(ToastBootstrap.toastHome, searchDir);
            search.mkdirs();
            File[] groovy = search.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".groovy");
                }
            });

            if (groovy != null)
                for (File file : groovy) {
                    try {
                        GroovyObject object = loadFile(file);
                        if (object instanceof GroovyScript) {
                            GroovyScript script = (GroovyScript) object;
                            script.loadScript();
                            scripts.add(script);
                        }
                        groovyFiles.put(file, object);
                    } catch (Exception e) {
                        logger.error("Could not load Groovy Script: " + file.getName());
                        logger.exception(e);
                    }
                }
        } catch (Exception e) {
            logger.error("Could not load Groovy Scripts: ");
            logger.exception(e);
        }
    }

    public static GroovyObject loadFile(File file) throws IOException, IllegalAccessException, InstantiationException {
        Class groovyClass = gLoader.parseClass(file);
        GroovyObject object = (GroovyObject) groovyClass.newInstance();

        try {
            object.invokeMethod("init", new Object[0]);
        } catch (Exception e) {}
        return object;
    }

    public static void prestart() {
        for (GroovyScript script : scripts) {
            script.prestartRobot();
        }
    }

    public static void start() {
        for (GroovyScript script : scripts) {
            script.startRobot();
        }
    }

}
