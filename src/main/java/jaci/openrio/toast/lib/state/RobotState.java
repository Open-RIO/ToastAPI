package jaci.openrio.toast.lib.state;

public enum RobotState {
    DISABLED("disabled"),
    TELEOP("teleop"),
    AUTONOMOUS("autonomous"),
    TEST("test");

    public String state;

    RobotState(String state) {
        this.state = state;
    }

}
