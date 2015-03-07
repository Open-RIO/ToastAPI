package jaci.openrio.toast.core.loader.groovy;

import groovy.lang.GroovyObject;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import jaci.openrio.toast.core.ToastBootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Properties;

/**
 * The class for all Groovy-Based preference files. Groovy preference files
 * allow for properties to be enclosed in closures, extend each other and even
 * have invoke-able methods, allowing for a wide range of flexibility. Implement
 * this class if you wish to use these configs
 *
 * @author Jaci
 */
public class GroovyPreferences {

    static File baseFile;

    public static void init() {
        baseFile = new File(ToastBootstrap.toastHome, "preferences");
        baseFile.mkdirs();
    }

    public GroovyObject parentObject;
    public ConfigObject config;

    /**
     * Get an existing preferences file, or create a new one if it doesn't
     * already exist
     * @param filename The name of the file. e.g. "ModuleName"
     */
    public GroovyPreferences(String filename) {
        try {
            if (!filename.contains("."))
                filename = filename + ".groovy";
            File file = new File(baseFile, filename);
            if (!file.exists())
                file.createNewFile();

            parentObject = GroovyLoader.loadFile(file);
            config = new ConfigSlurper().parse(file.toURI().toURL());
        } catch (Exception e) {
            GroovyLoader.logger.error("Could not load Preferences File: " + filename);
            GroovyLoader.logger.exception(e);
        }
    }

    /**
     * Get a PreferenceObject in its raw form from a key
     */
    public Object getPreferenceObject(String key) {
        String[] s = key.split("\\.");
        ConfigObject object = null;
        for (int i = 0; i < s.length; i++) {
            if (i == 0 && s.length != 1) {
                object = (ConfigObject) prop(config, s[i]);
                continue;
            } else if (s.length == 1) object = config;
            if (i == s.length - 1) return prop(object, s[i]);
            object = (ConfigObject) prop(object, s[i]);
        }
        return object;
    }

    static Object prop(ConfigObject object, String key) {
        if (object.containsKey(key)) {
            return object.getProperty(key);
        }
        return null;
    }

    /**
     * Invoke a method in the preferences file
     * @param methodName The name of the method to invoke
     * @param args The arguments to invoke with
     * @return The return value of the method, or Void if void type
     */
    public Object invokePreferenceMethod(String methodName, Object... args) {
        return parentObject.invokeMethod(methodName, args);
    }

    // PRIMS //

    /**
     * Get a number with the given key
     */
    public Number getNumber(String key) {
        Number number = (Number) getPreferenceObject(key);
        if (number == null) number = 0;
        return number;
    }

    /**
     * Get an integer with the given key
     */
    public int getInt(String key) {
        return getNumber(key).intValue();
    }

    /**
     * Get a double with the given key
     */
    public double getDouble(String key) {
            return getNumber(key).doubleValue();
    }

    /**
     * Get a float with the given key
     */
    public float getFloat(String key) {
        return getNumber(key).floatValue();
    }

    /**
     * Get a byte with the given key
     */
    public byte getByte(String key) {
        return getNumber(key).byteValue();
    }

    /**
     * Get a string with the given key
     */
    public String getString(String key) {
        return (String) getPreferenceObject(key);
    }

}
