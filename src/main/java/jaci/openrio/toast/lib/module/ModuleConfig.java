package jaci.openrio.toast.lib.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.script.js.JavaScript;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.io.*;
import java.util.HashMap;

public class ModuleConfig {

    static File base_file;
    static Bindings js_bind;

    public static void init() {
        base_file = new File(ToastBootstrap.toastHome, "config");
        base_file.mkdirs();
        js_bind = JavaScript.getEngine().createBindings();
        try {
            JavaScript.getEngine().eval(JavaScript.getSystemLib("Config.js"), js_bind);
        } catch (ScriptException e) {
        }
    }

    File parent_file;
    Bindings bindings;
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

            load();
        } catch (Exception e) { }
    }

    public ModuleConfig(File file) {
        try {
            this.parent_file = file;
            if (!file.exists())
                file.createNewFile();

            load();
        } catch (Exception e) { }
    }

    public void load() throws IOException, ScriptException {
        bindings = JavaScript.getEngine().createBindings();
        bindings.putAll(js_bind);
        JavaScript.copyBindings(bindings);
        defaults = new HashMap<>();
        try {
            JavaScript.getEngine().eval("_config = {}", bindings);
            reload();
        } catch (Exception e) { }
    }

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
        bindings.put("__json_defaults", json_defaults);
        bindings.put("__json_config", json_config);
        try {
            JavaScript.getEngine().eval("_config = parse(__json_defaults, __json_config)", bindings);
            PrintStream stream = new PrintStream(parent_file);
            String json = toJSON();
            stream.println(json);
            stream.close();
        } catch (Exception e) {
        }
    }

    public Object postProcess(Object o) {
        if (o instanceof String) {
            String str = (String) o;
            try {
                str = JavaScript.getEngine().eval("postProcess(\"" + str + "\");", bindings).toString();
            } catch (ScriptException e) {}
            return str;
        } else return o;
    }

    public Object getObject(String name) {
        try {
            Object o = JavaScript.getEngine().eval("_config." + name, bindings);
            o = postProcess(o);
            return o;
        } catch (ScriptException e) { }
        return null;
    }

    public String toJSON() throws ScriptException {
        return (String) JavaScript.getEngine().eval("toJSON(_config)", bindings);
    }

    public boolean has(String name) {
        return getObject(name) != null;
    }

    public void putDefault(String key, Object value) {
        defaults.put(key, value);
        reload();
    }

    public Object getOrDefault(String name, Object def) {
        if (!has(name)) {
            putDefault(name, def);
        }
        return getObject(name);
    }

    public Number getNumber(String name, Number def) {
        return (Number) getOrDefault(name, def);
    }

    public int getInt(String name, int def) {
        return getNumber(name, def).intValue();
    }

    public double getDouble(String name, double def) {
        return getNumber(name, def).doubleValue();
    }

    public float getFloat(String name, float def) {
        return getNumber(name, def).floatValue();
    }

    public long getLong(String name, long def) {
        return getNumber(name, def).longValue();
    }

    public byte getByte(String name, byte def) {
        return getNumber(name, def).byteValue();
    }

    public String getString(String name, String def) {
        return (String) getOrDefault(name, def);
    }

    public boolean getBoolean(String name, boolean def) {
        return (boolean) getOrDefault(name, def);
    }

    public Object get(String name, Object def) {
        return getOrDefault(name, def);
    }

}
