package jaci.openrio.toast.core.thread;

/**
 * A listening interface for the {@link jaci.openrio.toast.core.thread.Heartbeat} service. Classes or Anonymous
 * Functions that implement this will be ticked on the Heartbeat every beat, assuming they are registered with
 * {@link jaci.openrio.toast.core.thread.Heartbeat#add}
 *
 * Functions inside of this interface should be Thread-Safe
 *
 * Also known as the 'Stethoscope' class
 * 
 * @author Jaci
 */
public interface HeartbeatListener {

    /**
     * Called when the Heartbeat ticks
     *
     * @param skipped The number of skipped beats. If the Heartbeat takes too long
     *                to execute, the Heartbeat will purposefully 'skip' a beat and
     *                move on to the next one to ensure timing stays correct. This
     *                parameter will give you the amount of skipped beats since the last
     *                successful beat.
     */
    public void onHeartbeat(int skipped);

}
