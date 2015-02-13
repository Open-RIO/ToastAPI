package jaci.openrio.toast.core.monitoring.power;

import edu.wpi.first.wpilibj.ControllerPower;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import jaci.openrio.toast.lib.EvictingQueue;
import jaci.openrio.toast.lib.log.Logger;

import static jaci.openrio.toast.lib.math.MathHelper.round;

/**
 * Monitors the Power Distribution Panel, keeping a record of voltages and amperage to the last 10 ticks.
 *
 * When a brownout occurs, it gathers the largest current draw and reports it in the log
 *
 * @author Jaci
 */
public class PDPMonitor {

    private static EvictingQueue<PDPPoll> pollHistory = new EvictingQueue<PDPPoll>(10);

    static PowerDistributionPanel pdp;

    static Logger log;

    /**
     * Initialize the PDPMonitor. This is managed by Toast
     */
    public static void init() {
        pdp = new PowerDistributionPanel();
        log = new Logger("Toast|Power", Logger.ATTR_DEFAULT);
    }

    /**
     * Reset the faults on the Power Distribution Panel. A 'sticky fault' occurs when the robot browns-out.
     * To identify a sticky fault, the lights on the PDP will flash orange. When they are green, the faults are cleared
     */
    public static void resetFaults() {
        pdp.clearStickyFaults();
    }

    /**
     * @return Are we currently browned-out?
     */
    public static boolean brownout() {
        return !ControllerPower.getEnabled3V3() || !ControllerPower.getEnabled5V() || !ControllerPower.getEnabled6V();
    }

    /**
     * @return The under-lying {@link edu.wpi.first.wpilibj.PowerDistributionPanel} instance
     */
    public static PowerDistributionPanel panel() {
        return pdp;
    }

    public static void tick() {
        double[] current = new double[16];
        for (int i = 0; i < 16; i++) {
            current[i] = pdp.getCurrent(i);
        }
        PDPPoll poll = new PDPPoll(ControllerPower.getInputVoltage(), current);
        pollHistory.add(poll);

        if (!ControllerPower.getEnabled3V3() || !ControllerPower.getEnabled5V() || !ControllerPower.getEnabled6V()) {
            //We've browned-out
            PDPPoll highest = null;
            PDPPoll lowestVoltage = null;
            PDPPoll[] array =  pollHistory.toArray(new PDPPoll[0]);
            for (PDPPoll p : array) {
                if (p == null) continue;
                if (highest == null || p.totalCurrentDraw > highest.totalCurrentDraw)
                    highest = p;
                if (lowestVoltage == null || lowestVoltage.batteryVoltage > p.batteryVoltage)
                    lowestVoltage = p;
            }

            if (lowestVoltage != null && highest != null) {
                log.error("Robot has browned-out. Lowest Voltage: " + round(lowestVoltage.batteryVoltage, 2));
                log.error("\tHighest Current Draw: " + highest.totalCurrentDraw);
                for (int i = 0; i < highest.portCurrent.length; i++) {
                    log.error(String.format("\tPort %s current draw: %s", i, highest.portCurrent[i]) + (highest.highestIndex == i ? " (HIGHEST) " : ""));
                }
            }
        }
    }
}
