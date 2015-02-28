package jaci.openrio.toast.lib.crash;

/**
 * An interface for classes that wish to add custom information to crash logs.
 * Registered in {@link jaci.openrio.toast.lib.crash.CrashHandler}
 *
 * @author Jaci
 */
public interface CrashInfoProvider {

    /**
     * The name of the provider
     */
    public String getName();

    /**
     * The same as {@link #getCrashInfo}, but is done before the crash is logged.
     * Keep in mind this data is not appended with {@link #getName}
     *
     * @param t The exception encountered
     */
    public String getCrashInfoPre(Throwable t);

    /**
     * The information to append to the crash log
     *
     * @param t The exception encountered
     */
    public String getCrashInfo(Throwable t);

}
