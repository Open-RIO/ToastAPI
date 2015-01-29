package jaci.openrio.toast.lib.state;

import jaci.openrio.toast.lib.state.RobotState;

public interface StateListener {

    public static interface Ticker {
        public void tickState(RobotState state);
    }

    public static interface Transition {
        public void transitionState(RobotState state, RobotState oldState);
    }

}
