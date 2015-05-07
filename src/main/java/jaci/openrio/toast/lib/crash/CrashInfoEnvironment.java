package jaci.openrio.toast.lib.crash;

import jaci.openrio.toast.lib.Version;

import java.util.ArrayList;
import java.util.List;

import static jaci.openrio.toast.core.Environment.*;

/**
 * A CrashInfoProvider that contains information about the Environment. This includes things like
 * Toast Version, OS Version/Arch, Environment Type (sim, verification, robot), Java details and
 * FMS connectivity.
 *
 * @author Jaci
 */
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
        list.add(String.format("%10s %s", "Toast:", Version.version().get()));
        list.add(String.format("%10s %s", "Type:", getEnvironmentalType()));
        list.add(String.format("%10s %s", "FMS:", isCompetition()));
        list.add(String.format("%10s %s %s (%s)", "OS:", getOS_Name(), getOS_Version(), getOS_Architecture()));
        list.add(String.format("%10s %s (%s)", "Java:", getJava_version(), getJava_vendor()));
        list.add(String.format("%10s %s", "Java Path:", getJava_home()));
        return list;
    }
}
