package jaci.openrio.toast.lib.crash;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.loader.module.ModuleCandidate;
import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.core.loader.module.ModuleManager;
import jaci.openrio.toast.lib.module.ToastModule;

import java.util.ArrayList;
import java.util.Arrays;

public class CrashInfoToast implements CrashInfoProvider {
    @Override
    public String getName() {
        return "Toast";
    }

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

    @Override
    public String getCrashInfo(Throwable t) {
        StringBuilder builder = new StringBuilder();

        builder.append("\tLoaded Modules: \n");
        for (ModuleContainer module : ModuleManager.getContainers())
            builder.append("\t\t\t" + module.getDetails() + "\n");

        builder.append("\n\t\tEnvironment Status: \n");
        builder.append("\t\t\t" + (ToastBootstrap.isSimulation ? "Simulation" : "Normal Deployment") + "\n");
        return builder.toString();
    }
}
