package jaci.openrio.toast.lib.crash;

import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.core.loader.module.ModuleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CrashInfoProvider for Modules. This prints out important information about the modules, including the
 * packages they own and other details.
 *
 * @author Jaci
 */
public class CrashInfoModules implements CrashInfoProvider {

    @Override
    public String getName() {
        return "Modules";
    }

    /**
     * The same as {@link #getCrashInfo}, but is done before the crash is logged.
     * Keep in mind this data is not appended with {@link #getName}
     *
     * @param t The exception encountered
     */
    @Override
    public String getCrashInfoPre(Throwable t) {
        return null;
    }

    /**
     * The information to append to the crash log
     *
     * @param t The exception encountered
     */
    @Override
    public List<String> getCrashInfo(Throwable t) {
        ArrayList<String> text = new ArrayList<>();

        for (ModuleContainer module : ModuleManager.getContainers()) {
            try {
                text.add(module.getName());
                text.add("\tName: " + module.getName());
                text.add("\tVersion: " + module.getVersion());
                text.add("\tFile: " + module.getCandidate().getModuleFile());
                text.add("\tBypass Class: " + module.getCandidate().getBypassClass());
                text.add("\tCore Module Class: " + module.getCandidate().getCorePluginClass());
                text.add("\tOwned Packages: ");
                for (String pack : module.getCandidate().getClassEntries())
                    text.add("\t\t" + pack);

                HashMap<String, String> customData = module.getModule().getCustomData();
                if (customData != null) {
                    text.add("\tCustom Data: ");
                    for (Map.Entry<String, String> entry : customData.entrySet())
                        text.add("\t\t" + entry.getKey() + ": " + entry.getValue());
                }
            } catch (Throwable e) {
                text.add(module.getClass().getCanonicalName() + " could not be logged. (" + e.getLocalizedMessage() + ")");
            }
        }

        return text;
    }
}
