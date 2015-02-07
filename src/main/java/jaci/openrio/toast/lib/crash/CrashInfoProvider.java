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
     * The information to append to the crash log
     *
     * @param t The exception encountered
     */
    public String getCrashInfo(Throwable t);

}
