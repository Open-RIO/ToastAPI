package jaci.openrio.toast.lib.state;

import jaci.openrio.toast.lib.state.RobotState;

/**
 * A super-class for interfaces wanting to trigger methods when the robot ticks or transitions between states
 *
 * @author Jaci
 */
public interface StateListener {

    /**
     * An interface for classes wanting to trigger when the Robot 'ticks' a state, either when new data is available
     * or once every 20ms.
     */
    public static interface Ticker {
        public void tickState(RobotState state);
    }

    /**
     * An interface for classes wanting to trigger when the Robot 'transitions' between states
     */
    public static interface Transition {
        public void transitionState(RobotState state, RobotState oldState);
    }

}
