package jaci.openrio.toast.core.loader.groovy;

import groovy.lang.GroovyObject;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import jaci.openrio.toast.core.ToastBootstrap;

import java.io.*;
import java.util.Arrays;
import java.util.List;

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
    File parentFile;

    /**
     * Get an existing preferences file, or create a new one if it doesn't
     * already exist
     * @param filename The name of the file. e.g. "ModuleName"
     */
    public GroovyPreferences(String filename) {
        try {
            if (!filename.contains("."))
                filename = filename + ".conf";
            parentFile = new File(baseFile, filename);
            if (!parentFile.exists())
                parentFile.createNewFile();

            load();
        } catch (Exception e) {
            GroovyLoader.logger.error("Could not load Preferences File: " + filename);
            GroovyLoader.logger.exception(e);
        }
    }

    public GroovyPreferences(File file) {
        try {
            this.parentFile = file;
            if (!file.exists())
                file.createNewFile();

            load();
        } catch (Exception e) {
            GroovyLoader.logger.error("Could not load Preferences File: " + file);
            GroovyLoader.logger.exception(e);
        }
    }

    public void load() throws IllegalAccessException, IOException, InstantiationException {
        parentObject = GroovyLoader.loadFile(parentFile);
        config = new ConfigSlurper().parse(parentFile.toURI().toURL());
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
        if (object != null && object.containsKey(key)) {
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

    /**
     * Write a key to the end of the file
     */
    public void writeKey(String key, Object value, String... comment) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(parentFile, true)));
            value = convertValue(value);
            if (comment != null)
                for (String c : comment) out.println("// " + c);
            out.println(key + " = " + value);
            out.println();
            out.close();
        } catch (IOException e) { }

    }

    public Object convertValue(Object value) {
        if (value instanceof String)
            value = "\"" + value + "\"";
        if (value instanceof List) {
            convertValue(((List) value).toArray());
        } else if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            Object[] v = new Object[array.length];
            for (int i = 0; i < array.length; i++)
                v[i] = convertValue(array[i]);
            return Arrays.toString(v);
        }
        return value;
    }

    // PRIMS //

    /**
     * Returns whether a key exists in the configuration
     */
    public boolean keyExists(String key) {
        return getPreferenceObject(key) != null;
    }

    /**
     * Get an object, with a default value if it doesn't exist
     */
    public Object getObject(String key, Object defaultValue, String... comment) {
        Object val = defaultValue;
        if (keyExists(key))
            val = getPreferenceObject(key);
        else {
            writeKey(key, defaultValue, comment);
            try {
                load();
                val = getPreferenceObject(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return val;
    }

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

    /**
     * Get a boolean with the given key
     */
    public boolean getBoolean(String key) {
        return (boolean) getPreferenceObject(key);
    }

    /**
     * Get a number, but create it if it does not exist.
     */
    public Number getNumber(String key, Number defaultValue, String... comment) {
        if (keyExists(key))
            return getNumber(key);
        else
            writeKey(key, defaultValue, comment);
        return defaultValue;
    }

    /**
     * Get a string, but create it if it does not exist.
     */
    public String getString(String key, String defaultValue, String... comment) {
        if (keyExists(key))
            return getString(key);
        else
            writeKey(key, defaultValue, comment);
        return defaultValue;
    }

    /**
     * Get a string, but create it if it does not exist.
     */
    public boolean getBoolean(String key, boolean defaultValue, String... comment) {
        if (keyExists(key))
            return getBoolean(key);
        else
            writeKey(key, defaultValue, comment);
        return defaultValue;
    }

    /**
     * Get an integer with the given key, but create it if it doesn't exist
     */
    public int getInt(String key, int defaultValue, String... comment) {
        return getNumber(key, defaultValue, comment).intValue();
    }

    /**
     * Get a double with the given key, but create it if it doesn't exist
     */
    public double getDouble(String key, double defaultValue, String... comment) {
        return getNumber(key, defaultValue, comment).doubleValue();
    }

    /**
     * Get a float with the given key, but create it if it doesn't exist
     */
    public float getFloat(String key, float defaultValue, String... comment) {
        return getNumber(key, defaultValue, comment).floatValue();
    }

    /**
     * Get a byte with the given key, but create it if it doesn't exist
     */
    public byte getByte(String key, byte defaultValue, String... comment) {
        return getNumber(key, defaultValue, comment).byteValue();
    }

}
