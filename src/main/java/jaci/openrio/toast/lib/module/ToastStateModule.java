package jaci.openrio.toast.lib.module;

import jaci.openrio.toast.core.StateTracker;
import jaci.openrio.toast.core.loader.annotation.NoLoad;
import jaci.openrio.toast.lib.state.RobotState;
import jaci.openrio.toast.lib.state.StateListener;

/**
 * An implementation of {@link jaci.openrio.toast.lib.module.ToastModule} that includes StateListeners for both
 * Ticking and Transitioning. Implement this if you're used to {@link edu.wpi.first.wpilibj.IterativeRobot}
 *
 * @see jaci.openrio.toast.lib.state.StateListener
 * @see jaci.openrio.toast.core.StateTracker
 *
 * @author Jaci
 */
@NoLoad
public abstract class ToastStateModule extends ToastModule implements StateListener.Ticker, StateListener.Transition {

    /**
     * Called when this Module has been discovered and constructed. This method isn't usually used for much, but
     * can be useful for triggering things before the robot is in Pre-Initialization.
     */
    public void onConstruct() {
        StateTracker.addTicker(this);
        StateTracker.addTransition(this);
    }

    /**
     * Called when a transition between 2 states occurs. This involves the robot migrating from a state (such as disabled)
     * to another state (such as autonomous).
     * @param state     The new state the robot is in
     * @param oldState  The state the robot was in before the transition (may be null)
     */
    public void transitionState(RobotState state, RobotState oldState) {}

}
