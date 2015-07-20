package jaci.openrio.toast.core.script;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.io.Storage;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The ScriptLoader is used for loading Files from the FileSystem into a ScriptEngine instance. This automatically manages
 * retrieving of Files and evaluating them into the engine. USB Mass Storage rules are also in effect inside of the Script Loader.
 *
 * @author Jaci
 */
public class ScriptLoader {

    /**
     * Get the directory for the script root. This is based from toast/script/. This does NOT mkdir(), you must call
     * that yourself if you intend to use the file.
     */
    public static File getScriptDirByType(String type) {
        return new File(ToastBootstrap.toastHome, "script/" + type);
    }

    /**
     * Load all the scripts in the given directory into the provided ScriptEngine. Only files with the defined filenames
     * will be accepted into the loader. This function will recursively search in each Sub-Directory of the parent directory
     */
    public static List<String> loadAll(String homedir, ScriptEngine engine, String... filenames) throws FileNotFoundException {
        List<String> list = new ArrayList<String>();
        if (!USBMassStorage.overridingModules())
            searchModules(new File(getScriptDirByType(homedir), "modules"), engine);

        for (MassStorageDevice device : USBMassStorage.connectedDevices) {
            if (device.concurrent_modules || device.override_modules)
                searchModules(new File(new File(device.toast_directory, "script/" + homedir), "modules"), engine);
        }

        if (!USBMassStorage.overridingModules())
            search(getScriptDirByType(homedir), engine, list, filenames);

        for (MassStorageDevice device : USBMassStorage.connectedDevices) {
            if (device.concurrent_modules || device.override_modules)
                search(new File(device.toast_directory, "script/" + homedir), engine, list, filenames);
        }
        return list;
    }

    /**
     * Recursively search a directory (and its subdirectory) for candidate files and load them into the ScriptEngine.
     */
    private static void search(File dir, ScriptEngine engine, List<String> list, String... filenames) throws FileNotFoundException {
        dir.mkdirs();

        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (new File(dir, name).isDirectory()) return true;
                for (String ext : filenames)
                    if (name.equalsIgnoreCase(ext)) return true;
                return false;
            }
        });

        if (files != null)
            for (File file : files) {
                if (!file.isDirectory()) {
                    try {
                        load(file, engine);
                    } catch (ScriptException e) {
                        Toast.log().error("Could not load Script: " + file);
                        Toast.log().exception(e);
                    }
                }
            }
    }

    private static void searchModules(File dir, ScriptEngine engine) {
        dir.mkdirs();
        HashMap<String, File> map = new HashMap<>();
        File extraction = new File(dir, ".extraction_cache");
        if (extraction.exists())
            extraction.delete();
        extraction.mkdirs();

        File[] modules = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jsm");
            }
        });

        if (modules != null)
            for (File file : modules) {
                try {
                    File extract = new File(extraction, file.getName().replace(".jsm", ""));
                    unzip(file, extract);
                    mapModule(extract, map);
                } catch (IOException e) { }
            }
        engine.put("__MODULES", map);
    }

    private static void mapModule(File directory, HashMap<String, File> map) throws FileNotFoundException {
        File metadata = new File(directory, "module.json");
        JsonObject obj = new JsonParser().parse(new FileReader(metadata)).getAsJsonObject();
        map.put(obj.get("name").getAsString(), new File(directory, obj.get("script").getAsString()).getAbsoluteFile());
    }

    private static void unzip(File source, File target) throws IOException {
        ZipFile zf = new ZipFile(source);
        Enumeration<? extends ZipEntry> entries = zf.entries();
        while (entries.hasMoreElements()) {
            ZipEntry ze = entries.nextElement();
            if (ze.isDirectory()) {
                new File(target, ze.getName()).mkdirs();
            } else {
                File out = new File(target, ze.getName());
                InputStream is = zf.getInputStream(ze);
                FileOutputStream fos = new FileOutputStream(out);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                is.close();
                fos.close();
            }
        }
        zf.close();
    }

    /**
     * Load a single File and return the value it returns. This is used by the require keyword among others. This occurs across all USB devices
     */
    public static Object loadSingle(String homedir, ScriptEngine engine, String file) throws FileNotFoundException, ScriptException {
        final Object[] returnVal = {null};
        Storage.USB_Module(new File("script/" + homedir, file).toString(), new Storage.StorageCallback() {
            @Override
            public void call(File file, boolean isUSB, MassStorageDevice device) {
                if (file.exists()) {
                    try {
                        returnVal[0] = load(file, engine);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return returnVal[0];
    }

    public static Object loadModule(String homedir, ScriptEngine engine, String name) throws FileNotFoundException, ScriptException {
        HashMap<String, File> mp = (HashMap<String, File>) engine.get("__MODULES");
        return load(mp.get(name), engine);
    }

    public static Object loadRelative(String basedir, ScriptEngine engine, String loadtarget) {
        String rel = USBMassStorage.relativize(new File(basedir));
        final Object[] load = {null};
        Storage.USB_Module(rel, new Storage.StorageCallback() {
            @Override
            public void call(File file, boolean isUSB, MassStorageDevice device) {
                File loadPath = new File((file.isDirectory() ? file : file.getParentFile()), loadtarget);
                if (loadPath.exists()) {
                    try {
                        load[0] = load(loadPath, engine);
                    } catch (FileNotFoundException | ScriptException e) { }
                }
            }
        });
        return load[0];
    }

    public static Object loadHere(String basedir, ScriptEngine engine, String loadtarget) throws FileNotFoundException, ScriptException {
        File file = new File(basedir);
        file = (file.isDirectory() ? file : file.getParentFile());
        return load(new File(file, loadtarget), engine);
    }

    /**
     * Load a file into the script engine. This uses the load() keyword in nashorn to ensure the __FILE__ and __DIR__ attributes are
     * added correctly
     */
    public static Object load(File file, ScriptEngine engine) throws FileNotFoundException, ScriptException {
        try {
            engine.put("__LOAD_TARGET", file.getAbsolutePath());
            return engine.eval("load(__LOAD_TARGET)");
        } finally {
            engine.eval("delete __LOAD_TARGET;");
        }
    }
}