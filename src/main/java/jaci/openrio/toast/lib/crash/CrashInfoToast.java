package jaci.openrio.toast.lib.crash;

import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.core.loader.module.ModuleManager;
import jaci.openrio.toast.lib.Version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A CrashInfoProvider that gives data about Toast and modules loaded.
 *
 * @link Jaci
 */
public class CrashInfoToast implements CrashInfoProvider {

    /**
     * The name of the provider
     */
    @Override
    public String getName() {
        return "Toast";
    }

    /**
     * The same as {@link #getCrashInfo}, but is done before the crash is logged.
     * Keep in mind this data is not appended with {@link #getName}
     *
     * @param t The exception encountered
     */
    @Override
    public String getCrashInfoPre(Throwable t) {
        StringBuilder builder = new StringBuilder();

        ArrayList<String> culprits = new ArrayList<>();
        for (StackTraceElement element : t.getStackTrace()) {
            String clazz = element.getClassName();
            if (clazz.startsWith("jaci.openrio.toast.core") || clazz.startsWith("jaci.openrio.toast.lib")) {
                if (!culprits.contains("Toast"))
                    culprits.add("Toast");
            }
            for (ModuleContainer container : ModuleManager.getContainers()) {
                if (container.getCandidate() != null && Arrays.asList(container.getCandidate().getClassEntries()).contains(clazz) && culprits.contains(container.getDetails())) {
                    culprits.add(container.getDetails());
                }
            }
        }

        if (!culprits.isEmpty()) {
            builder.append("Suspected Culprits for this Crash are: ");
            boolean hasBefore = false;
            for (String culprit : culprits) {
                builder.append((hasBefore ? ", " : "") + culprit);
                hasBefore = true;
            }
        }

        return builder.toString();
    }

    /**
     * The information to append to the crash log
     *
     * @param t The exception encountered
     */
    @Override
    public List<String> getCrashInfo(Throwable t) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Toast Version: " + Version.version().get());
        list.add("Loaded Modules:");
        for (ModuleContainer module : ModuleManager.getContainers())
            list.add("\t" + module.getDetails());

        return list;
    }
}
