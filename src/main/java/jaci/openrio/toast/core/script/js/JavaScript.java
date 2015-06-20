package jaci.openrio.toast.core.script.js;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.HashMap;

public class JavaScript {

    static ScriptEngineManager manager;
    static ScriptEngine engine;
    static String engine_type;

    public static void init() {
        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        if (engine == null) {
            engine = manager.getEngineByName("rhino");
            engine_type = "Rhino";
        } else engine_type = "Nashorn";
    }

    public static Object eval(String script) throws ScriptException {
        return engine.eval(script);
    }

    public static Object eval(Reader reader) throws ScriptException {
        return engine.eval(reader);
    }

    public static void put(String key, Object o) {
        engine.put(key, o);
    }

    public static Object get(String key) {
        return engine.get(key);
    }

    public static boolean supported() {
        return engine != null;
    }

    public static ScriptEngine getEngine() {
        return engine;
    }

    public static String engineType() {
        return engine_type;
    }

    public static String jsonify(Object o) {
        return jsonify(o, true);
    }
    public static String jsonify(Object o, boolean prettyprint) {
        return jsonify(o, prettyprint, 2);
    }
    public static String jsonify(Object o, boolean prettyprint, int indent) {
        try {
            HashMap<String, Object> vals = JsonEngine.convert(o);
            Bindings bind = toNative(vals);
            bind.put("indent", indent);
            if (prettyprint)
                return (String) engine.eval("JSON.stringify(jsobj, null, indent)", bind);
            else
                return (String) engine.eval("JSON.stringify(jsobj)", bind);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bindings toNative(HashMap<String, Object> map) throws ScriptException {
        Bindings bind = engine.createBindings();
        bind.put("map_obj", map);
        engine.eval("var jsobj = {}; for (obj in map_obj) { jsobj[obj] = map_obj[obj] }", bind);
        return bind;
    }

}
