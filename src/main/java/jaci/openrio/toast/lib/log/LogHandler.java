package jaci.openrio.toast.lib.log;

/**
 * An interface that attaches to all instances of {@link jaci.openrio.toast.lib.log.Logger}
 *
 * This class is notified when a message is logged
 *
 * @author Jaci
 */
public interface LogHandler {

    /**
     * Called after a message is logged. Use this to send data to your driver-station or whatever else
     */
    public void onLog(String level, String message, String formatted, Logger logger);

}
