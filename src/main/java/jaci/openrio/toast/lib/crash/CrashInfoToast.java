package jaci.openrio.toast.lib.crash;

import groovy.lang.GroovyObject;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.loader.groovy.GroovyLoader;
import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.core.loader.module.ModuleManager;
import jaci.openrio.toast.lib.module.GroovyScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public List<String> getCrashInfo(Throwable t) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Loaded Modules:");
        for (ModuleContainer module : ModuleManager.getContainers())
            list.add("\t" + module.getDetails());

        list.add("Loaded Groovy Scripts:");
        for (GroovyScript script : GroovyLoader.scripts)
            list.add("\t" + script.getClass());

        list.add("Loaded Groovy Files:");
        for (Map.Entry<String, GroovyObject> entry : GroovyLoader.groovyObjects.entrySet())
            list.add("\t" + entry.getKey() + " : " + entry.getValue());

        return list;
    }
}
