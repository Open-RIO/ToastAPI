package jaci.openrio.toast.lib.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.script.js.JavaScript;
import jaci.openrio.toast.lib.profiler.Profiler;
import jaci.openrio.toast.lib.profiler.ProfilerSection;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The ModuleConfig is the new replacement for the old GroovyPreferences.
 * The ModuleConfig class allows for modules to take user-defined preferences regarding how the module functions.
 * The Configuration Files are stored under toast/config/ with the .conf file extension. These files are Formatted in JSON
 * and are automatically generated if the requested key does not exist.
 *
 * @author Jaci
 */
public class ModuleConfig {

    static File base_file;
    static Bindings js_bind;
    public static LinkedList<ModuleConfig> allConfigs;

    /**
     * Start the configuration engine. This sets up the Root dir as well as JavaScript bindings for Config.js.
     * This is executed by the Bootstrapper, no need to call it yourself.
     */
    public static void init() {
        ProfilerSection section = Profiler.INSTANCE.section("JavaScript");
        section.start("ModuleConfig");
        base_file = new File(ToastBootstrap.toastHome, "config");
        base_file.mkdirs();
        allConfigs = new LinkedList<>();
        js_bind = JavaScript.getEngine().createBindings();
        try {
            JavaScript.getEngine().eval(JavaScript.getSystemLib("Config.js"), js_bind);
            JavaScript.getEngine().eval(JavaScript.getSystemLib("Util.js"), js_bind);
            JavaScript.getEngine().eval("_config = {}", js_bind);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        section.stop("ModuleConfig");
    }

    File parent_file;
    HashMap<String, Object> defaults;

    public ModuleConfig(String name) {
        try {
            if (USBMassStorage.config_highest != null) {
                base_file = new File(USBMassStorage.config_highest.toast_directory, "config");
                base_file.mkdirs();
            }
            if (!name.contains("."))
                name = name + ".conf";
            parent_file = new File(base_file, name);
            if (!parent_file.exists())
                parent_file.createNewFile();

            allConfigs.add(this);
            load();
        } catch (Exception e) { }
    }

    public ModuleConfig(File file) {
        try {
            this.parent_file = file;
            if (!file.exists())
                file.createNewFile();

            allConfigs.add(this);
            load();
        } catch (Exception e) { }
    }

    /**
     * Load a ModuleConfig. This method will flush the Bindings of the configuration file
     * and reload defaults. This should only be called by the constructor
     */
    private void load() throws IOException, ScriptException {
        defaults = new HashMap<>();
        try {
            JavaScript.getEngine().eval("_config['" + hashCode() + "'] = {}", js_bind);
            reload();
        } catch (Exception e) { }
    }

    /**
     * Reloads the configuration file. This will read the config file, add keys if they exist in the Defaults
     * Queue, as well as reformat the config file to PrettyPrint if needed. Call this if Config Files have been
     * changed (by the user) and you want to reload them.
     */
    public void reload() {
        String json_config = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(parent_file));
            String ln;
            while ((ln = reader.readLine()) != null)
                json_config += ln;
            reader.close();
        } catch (Exception e) {
        }

        Gson gson_engine = new GsonBuilder().setPrettyPrinting().create();
        String json_defaults = gson_engine.toJson(defaults);
        js_bind.put("__json_defaults", json_defaults);
        js_bind.put("__json_config", json_config);
        try {
            JavaScript.getEngine().eval("_config['" + hashCode() + "'] = parse(__json_defaults, __json_config)", js_bind);
            PrintStream stream = new PrintStream(parent_file);
            String json = toJSON();
            stream.println(json);
            stream.close();
        } catch (Exception e) {
        }
    }

    /**
     * PostProcess an object. If the object is of type String, it will be passed to JavaScript to parse
     * out the ${} tags if they exist. This method is called after {@link #reload()}, so post processing
     * will not be shown in the Config File, meaning ${} tags won't be overridden by their result.
     */
    public Object postProcess(Object o) {
        if (o instanceof String) {
            String str = (String) o;
            try {
                str = JavaScript.getEngine().eval("postProcess(\"" + str + "\");", js_bind).toString();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            return str;
        } else return o;
    }

    /**
     * Get an object from the configuration file. This will also post-process the data. Will return null if it
     * does not yet exist.
     */
    public Object getObject(String name) {
        try {
            Object o = JavaScript.getEngine().eval("_config['" + this.hashCode() + "']." + name, js_bind);
            o = postProcess(o);
            return o;
        } catch (ScriptException e) { }
        return null;
    }

    /**
     * Convert the entire configuration into a JSON string. This does NOT post-process any information in the
     * configuration file, but will instead return the raw data as a JSONified string. This will also be pretty-printed
     */
    public String toJSON() throws ScriptException {
        return (String) JavaScript.getEngine().eval("toJSON(_config['" + hashCode() + "'])", js_bind);
    }

    @Override
    public int hashCode() {
        return parent_file.hashCode();
    }

    /**
     * Checks whether a key exists in the configuration file. Alias to a null check on {@link #getObject(String)}
     */
    public boolean has(String name) {
        return getObject(name) != null;
    }

    /**
     * Put a default key in the configuration. This is automatically called in most 'get' methods if the
     * key doesn't already exist in the configuration file.
     */
    public void putDefault(String key, Object value) {
        defaults.put(key, value);
        reload();
    }

    /**
     * Get a key from the config. If the key doesn't already exist, it will be created with the default value in the
     * configuration file.
     */
    public Object getOrDefault(String name, Object def) {
        if (!has(name)) {
            putDefault(name, def);
        }
        Object get = getObject(name);
        return get == null ? def : get;         //Just in case the File Write is locked for some reason
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}, coercing to a Number
     */
    public Number getNumber(String name, Number def) {
        return (Number) getOrDefault(name, def);
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}, coercing to an Integer
     */
    public int getInt(String name, int def) {
        return getNumber(name, def).intValue();
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}, coercing to a Double
     */
    public double getDouble(String name, double def) {
        return getNumber(name, def).doubleValue();
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}, coercing to a Float
     */
    public float getFloat(String name, float def) {
        return getNumber(name, def).floatValue();
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}, coercing to a Long
     */
    public long getLong(String name, long def) {
        return getNumber(name, def).longValue();
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}, coercing to a Byte
     */
    public byte getByte(String name, byte def) {
        return getNumber(name, def).byteValue();
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}, coercing to a String
     */
    public String getString(String name, String def) {
        return (String) getOrDefault(name, def);
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}, coercing to a Boolean
     */
    public boolean getBoolean(String name, boolean def) {
        return (boolean) getOrDefault(name, def);
    }

    /**
     * Get an array from the Configuration File
     */
    public <T> T[] getArray(String name, T[] def) {
        ScriptObjectMirror obj = ((ScriptObjectMirror) getOrDefault(name, def));
        return (T[]) obj.values().toArray(def);
    }

    /**
     * Alias for {@link #getOrDefault(String, Object)}
     */
    public Object get(String name, Object def) {
        return getOrDefault(name, def);
    }

}
