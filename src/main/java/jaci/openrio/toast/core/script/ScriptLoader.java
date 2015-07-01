package jaci.openrio.toast.core.script;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

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
     * Load all the scripts in the given directory into the provided ScriptEngine. Only files with the defined extensions
     * will be accepted into the loader. This function will recursively search in each Sub-Directory of the parent directory
     */
    public static List<String> loadAll(String homedir, ScriptEngine engine, String... extensions) throws FileNotFoundException {
        List<String> list = new ArrayList<String>();
        if (!USBMassStorage.overridingModules()) {
            search(getScriptDirByType(homedir), engine, list, extensions);
        }

        for (MassStorageDevice device : USBMassStorage.connectedDevices) {
            if (device.concurrent_modules || device.override_modules) {
                search(new File(device.drivePath, "script/" + homedir), engine, list, extensions);
            }
        }
        return list;
    }

    /**
     * Recursively search a directory (and its subdirectory) for candidate files and load them into the ScriptEngine.
     */
    private static void search(File dir, ScriptEngine engine, List<String> list, String... extensions) throws FileNotFoundException {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (new File(dir, name).isDirectory()) return true;
                for (String ext : extensions)
                    if (name.endsWith(ext)) return true;
                return false;
            }
        });

        if (files != null)
            for (File file : files) {
                if (!file.isDirectory()) {
                    try {
                        engine.eval(new FileReader(file));
                        Toast.log().info("Script Loaded: " + file.getName());
                    } catch (ScriptException e) {
                        Toast.log().error("Could not load Script: " + file);
                        Toast.log().exception(e);
                    }
                } else
                    search(file, engine, list, extensions);
            }
    }
}