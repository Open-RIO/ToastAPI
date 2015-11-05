package jaci.openrio.toast.lib.crash;

import jaci.openrio.toast.core.Environment;

import java.util.List;

/**
 * A CrashInfoProvider that contains information about the Environment. This includes things like
 * Toast Version, OS Version/Arch, Environment Type (sim, verification, robot), Java details and
 * FMS connectivity.
 *
 * @author Jaci
 */
public class CrashInfoEnvironment implements CrashInfoProvider {

    /**
     * The name of the provider
     */
    @Override
    public String getName() {
        return "Environment";
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
     * In this case, the information includes data gathered from the {@link jaci.openrio.toast.core.Environment} class
     *
     * @param t The exception encountered
     */
    @Override
    public List<String> getCrashInfo(Throwable t) {
        return Environment.toLines();
    }
}
