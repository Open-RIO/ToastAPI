package jaci.openrio.toast.lib.crash;

import jaci.openrio.toast.core.Toast;

import java.util.ArrayList;
import java.util.List;
import static jaci.openrio.toast.core.Environment.*;

public class CrashInfoEnvironment implements CrashInfoProvider {
    @Override
    public String getName() {
        return "Environment";
    }

    @Override
    public String getCrashInfoPre(Throwable t) {
        return null;
    }

    @Override
    public List<String> getCrashInfo(Throwable t) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Type: " + getEnvironmentalType());
        list.add("FMS: " + isCompetition());
        list.add(String.format("OS: %s %s (%s)", getOS_Name(), getOS_Version(), getOS_Architecture()));
        list.add(String.format("Java: %s (%s)", getJava_version(), getJava_vendor()));
        list.add("Java Path: " + getJava_home());
        return list;
    }
}
