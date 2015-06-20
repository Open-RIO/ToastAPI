package jaci.openrio.toast.core.script.js;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

/**
 * The Toast JavaScript engine. This class is responsible for managing JavaScript evaluation and execution. A single
 * ScriptEngine is created, and is selected with Nashorn if available, else it will fall back to Rhino. Modules may choose
 * to create their own ScriptEngines, but for many cases, this class should be used.
 *
 * @author Jaci
 */
public class JavaScript {

    static ScriptEngineManager manager;
    static ScriptEngine engine;
    static String engine_type;
    static Bindings toast_bindings;

    /**
     * Initialize the ScriptEngine and its manager. This is called by Toast, so don't worry about
     * calling it yourself.
     */
    public static void init() {
        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        if (engine == null) {
            engine = manager.getEngineByName("rhino");
            engine_type = "Rhino";
        } else engine_type = "Nashorn";
        toast_bindings = engine.createBindings();
    }

    /**
     * Evaluate a script. This will execute a script given and return the value of the script, with the default
     * bindings of the ScriptEngine. This is a shortcut for engine.eval()
     */
    public static Object eval(String script) throws ScriptException {
        checkSupported();
        return engine.eval(script);
    }

    /**
     * Evaluate a script. This will execute a script given and return the value of the script, with the default
     * bindings of the ScriptEngine. This is a shortcut for engine.eval()
     */
    public static Object eval(Reader reader) throws ScriptException {
        checkSupported();
        return engine.eval(reader);
    }

    /**
     * Put an object into the Engine's global bindings. This is the same as registering a 'var' in the JavaScript.
     */
    public static void put(String key, Object o) {
        checkSupported();
        engine.put(key, o);
    }

    /**
     * Get an object from the Engine's global bindings. This is the same as getting a variable from the JavaScript
     * engine.
     */
    public static Object get(String key) {
        checkSupported();
        return engine.get(key);
    }

    /**
     * Returns true if Scripting is supported on this platform. This will return false if a JavaScript engine is
     * not available (neither Nashorn or Rhino)
     */
    public static boolean supported() {
        return engine != null;
    }

    /**
     * Checks for supported(), and will throw an UnsupportedOperationException if false.
     */
    private static void checkSupported() {
        if (!supported()) throw new UnsupportedOperationException("ScriptEngine not supported in this JDK!");
    }

    /**
     * Get the Engine object itself if the shortcuts provided in this class aren't relevant. This gives full access
     * to all the ScriptEngine's features, including its bindings and other evaluation methods. Use this with caution
     */
    public static ScriptEngine getEngine() {
        return engine;
    }

    /**
     * Get the Engine Type. This is used in the Environment log for Crashes and things of the like. This returns either
     * 'Nashorn' or 'Rhino', and 'null' if supported() returns false.
     */
    public static String engineType() {
        return engine_type;
    }

    /**
     * JSONify an object with pretty print and indentation of 2
     */
    public static String jsonify(Object o) {
        return jsonify(o, true);
    }

    /**
     * JSONify an object with an option of pretty print, and indentation of 2
     */
    public static String jsonify(Object o, boolean prettyprint) {
        return jsonify(o, prettyprint, 2);
    }

    /**
     * Create a new ScriptEngine. This allows for modules to move away from the Bindings set by Toast and create their own
     * Scripting environment if they wish to. This allows for enhanced control over the JavaScript used in their code.
     */
    public static ScriptEngine createEngine() {
        ScriptEngine engine = manager.getEngineByName("nashorn");
        if (engine == null) {
            engine = manager.getEngineByName("rhino");
        }
        return engine;
    }

    /**
     * JSONify an object. This will take an object with any fields or methods with the @JSON
     * annotation, and get/invoke them, taking the value and formatting them as JSON. If the field or method returns
     * an Object that is NOT primitive (excl. String), the JsonEngine will search through that object for @JSON
     * annotation, and so on until the end of the tree is reached.
     *
     * @param o           The Object to JSONify
     * @param prettyprint Pretty Print (new lines and indentation)
     * @param indent      The indentation level for Pretty Print (space count)
     */
    public static String jsonify(Object o, boolean prettyprint, int indent) {
        checkSupported();
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

    /**
     * Converts a HashMap to a JS Object in a binding. The binding value is 'jsobj'
     */
    public static Bindings toNative(HashMap<String, Object> map) throws ScriptException {
        checkSupported();
        Bindings bind = engine.createBindings();
        bind.put("map_obj", map);
        engine.eval(new InputStreamReader(JavaScript.class.getResourceAsStream("/assets/toast/script/js/Map.js")), bind);
        engine.eval("var jsobj = hash_to_object(map_obj)", bind);
        return bind;
    }

}
