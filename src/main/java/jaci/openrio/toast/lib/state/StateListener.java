package jaci.openrio.toast.lib.state;

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
        /**
         * Called when a state is 'ticked' (periodically called). This is called once every 20ms, or once every contol
         * packet, whichever comes first.
         */
        public void tickState(RobotState state);
    }

    /**
     * An interface for classes wanting to trigger when the Robot 'transitions' between states
     */
    public static interface Transition {
        /**
         * Called when a transition between 2 states occurs. This involves the robot migrating from a state (such as disabled)
         * to another state (such as autonomous).
         * @param state     The new state the robot is in
         * @param oldState  The state the robot was in before the transition (may be null)
         */
        public void transitionState(RobotState state, RobotState oldState);
    }

}
