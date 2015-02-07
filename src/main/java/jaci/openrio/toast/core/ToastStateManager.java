package jaci.openrio.toast.core;

import jaci.openrio.toast.core.monitoring.power.PDPMonitor;
import jaci.openrio.toast.lib.state.RobotState;
import jaci.openrio.toast.lib.state.StateListener;

/**
 * Handles Toast's State-Dependant activities
 *
 * @author Jaci
 */
public class ToastStateManager implements StateListener.Ticker, StateListener.Transition {

    @Override
    public void tickState(RobotState state) {
        PDPMonitor.tick();
    }

    @Override
    public void transitionState(RobotState state, RobotState oldState) {
    }
}
