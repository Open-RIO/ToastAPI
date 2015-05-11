package jaci.openrio.toast.core.thread;

import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.state.ConcurrentVector;

/**
 * The 'Heartbeat' is a class that keeps a constant ticking routine. The timing between these routines is the same for each
 * tick, (20ms/50Hz), meaning tasks that require a scheduled timer may find this useful. Additionally, the Heartbeat will keep track
 * of the time required for each 'beat'. If a beat takes longer than the Heart Rate (20ms), the heartbeat will 'skip', and will
 * pass an argument to all the listeners that contains the number of beat(s) that were skipped. The difference in time between
 * each beat is therefore guaranteed to be a modulus of the Heart Rate (20ms).
 *
 * Additionally, the Heartbeat Thread will only be alive if there are Listeners in the array. This means the Heartbeat will
 * not tick if there is nothing to execute, making sure not to waste system resources.
 *
 * @author Jaci
 */
public class Heartbeat implements Runnable {

    static ConcurrentVector<HeartbeatListener> aorta = new ConcurrentVector<>();
    static long heart_rate = 20;
    static Logger nervous_system;
    int skipped_beats;
    static boolean running;
    int consecutive;

    @Override
    public void run() {
        log().info("Heartbeat started!");
        while (true) {
            try {
                running = true;
                long start = System.currentTimeMillis();
                aorta.tick();

                for (HeartbeatListener artery : aorta)
                    artery.onHeartbeat(skipped_beats);

                long end = System.currentTimeMillis();
                long tick_time = end - start;

                skipped_beats = 0;
                if (tick_time < heart_rate) {
                    Thread.sleep(heart_rate - tick_time);
                    consecutive = 0;
                } else if (tick_time > heart_rate) {
                    skipped_beats = (int)(tick_time / heart_rate);
                    consecutive++;
                    if (consecutive < 3) {
                        log().warn(String.format("Heartbeat skipped %s beats, (took %sms of max %sms)", skipped_beats, tick_time, heart_rate));
                    } else if (consecutive == 3) {
                        log().warn(String.format("Too many consecutive skipped Heartbeats, suppressing log."));
                    }
                    Thread.sleep(tick_time % heart_rate);
                }

                if (aorta.size() == 0) {
                    running = false;
                    log().info("No listeners in the queue, we've lost 'er, doc...");
                    return;
                }
            } catch (Exception e) {}
        }
    }

    /**
     * Get the logger for the Heartbeat
     */
    public static Logger log() {
        if (nervous_system == null)
            nervous_system = new Logger("Heartbeat", Logger.ATTR_TIME);
        return nervous_system;
    }

    /**
     * Add a listener to the Heartbeat. This can be done at any time
     */
    public static void add(HeartbeatListener artery) {
        aorta.addConcurrent(artery);
        checkActive();
    }

    /**
     * Remove a listener from the Heartbeat. This can be done at any time.
     */
    public static void remove(HeartbeatListener artery) {
        aorta.removeConcurrent(artery);
    }

    static Thread heart;

    static void checkActive() {
        if (!running) {
            heart = new Thread(new Heartbeat());
            heart.setName("Heartbeat");
            heart.start();
        }
    }
}
