package jaci.openrio.toast.core.script.js;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.core.script.ScriptLoader;

import javax.script.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

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
    static List<String> loadedScripts;

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
    }

    /**
     * Called when Toast is ready and bindings are ready to be bound.
     */
    public static void binderInit() {
        loadSystem();
        JSEngine.init();
    }

    /**
     * Initialize the Loader. This loads JS files from the FileSystem and imports them into the ScriptEngine. This allows
     * users to code in JavaScript if they wanted to for some hellish reason.
     */
    public static void loaderInit() {
        try {
            if (supported()) {
                ScriptLoader.getScriptDirByType("js").mkdirs();
                loadedScripts = ScriptLoader.loadAll("js", engine, ToastConfiguration.config.getArray("javascript.autoload", new String[] {"main.js"}));
            }
        } catch (Exception e) {
            Toast.log().error("Could not Load JavaScript script files: " + e);
            Toast.log().exception(e);
        }
    }

    /**
     * Load a file with the given name. This loads from the script/js directory
     */
    public static void loadFile(String file) {
        try {
            if (supported()) {
                ScriptLoader.getScriptDirByType("js").mkdirs();
                loadedScripts = ScriptLoader.loadAll("js", engine, (file.contains(".") ? file : file + ".js"));
            }
        } catch (Exception e) {
            Toast.log().error("Could not Load JavaScript script files: " + e);
            Toast.log().exception(e);
        }
    }

    /**
     * Load Toast's System .JS files before we load anything else.
     */
    private static void loadSystem() {
        try {
            put("__toast", Toast.getToast());

            loadSystemLib("Map.js");
            loadSystemLib("Toast.js");
        } catch (ScriptException e) {
            Toast.log().error("Could not load System Library: " + e);
            Toast.log().exception(e);
        }
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
     * Get a Toast System Library. These libraries are included with Toast by default and are found in
     * /assets/toast/script/js (dev env: src/main/resources/assets/toast/script/js/).
     */
    public static Reader getSystemLib(String name) {
        checkSupported();
        return new InputStreamReader(JavaScript.class.getResourceAsStream("/assets/toast/script/js/" + name));
    }

    /**
     * Load a Toast System Library. These libraries are included with Toast by default and are found in
     * /assets/toast/script/js (dev env: src/main/resources/assets/toast/script/js/).
     */
    public static Object loadSystemLib(String name) throws ScriptException {
        checkSupported();
        return engine.eval(getSystemLib(name));
    }

    /**
     * Copy Toast's bindings (loaded classes) to your own Bindings object to access all of Toast's SystemLibs.
     */
    public static void copyBindings(Bindings target) {
        Bindings source = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        target.putAll(source);
    }

}