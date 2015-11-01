package jaci.openrio.toast.lib.device;

import edu.wpi.first.wpilibj.Relay;

/**
 * A simple Relay wrapper class to set both Forward and Reverse simultaneously.
 */
public class SimpleRelay extends Relay {

    public SimpleRelay(int channel, Direction direction) {
        super(channel, direction);
    }

    public SimpleRelay(final int channel) {
        super(channel);
    }

    /**
     * Set the FWD and REV directions of the Relay independently
     */
    public void set(boolean forward, boolean reverse) {
        if (forward && reverse) {
            setDirection(Direction.kBoth);
            set(Value.kOn);
        } else if (!forward && !reverse) {
            setDirection(Direction.kBoth);
            set(Value.kOff);
        } else if (forward) {
            setDirection(Direction.kBoth);
            set(Value.kForward);
        } else {
            setDirection(Direction.kBoth);
            set(Value.kReverse);
        }
    }
}
