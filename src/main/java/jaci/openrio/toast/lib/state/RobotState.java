package jaci.openrio.toast.lib.state;

/**
 * An enumeration of all the states a robot can be in. Used in the
 * {@link jaci.openrio.toast.core.StateTracker} class
 *
 * @author Jaci
 */
public enum RobotState {
    DISABLED("Disabled"),
    TELEOP("Teleop"),
    AUTONOMOUS("Autonomous"),
    TEST("Test");

    public String state;

    RobotState(String state) {
        this.state = state;
    }

}
