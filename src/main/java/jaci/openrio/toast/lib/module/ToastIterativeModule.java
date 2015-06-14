package jaci.openrio.toast.lib.module;

import edu.wpi.first.wpilibj.Timer;
import jaci.openrio.toast.core.StateTracker;
import jaci.openrio.toast.core.loader.annotation.NoLoad;
import jaci.openrio.toast.lib.state.RobotState;
import jaci.openrio.toast.lib.state.StateListener;

/**
 * Provides an interface similar to {@link edu.wpi.first.wpilibj.IterativeRobot} for Toast Modules to
 * extend.
 *
 * @author Jaci
 */
@NoLoad
public abstract class ToastIterativeModule extends ToastStateModule {

    /**
     * Called when a state is 'ticked' (periodically called). This is called once every 20ms, or once every contol
     * packet, whichever comes first.
     */
    @Override
    public void tickState(RobotState state) {
        switch (state) {
            case DISABLED:
                disabledPeriodic();
                break;
            case AUTONOMOUS:
                autonomousPeriodic();
                break;
            case TELEOP:
                teleopPeriodic();
                break;
            case TEST:
                testPeriodic();
                break;
        }
    }

    /**
     * Called when a transition between 2 states occurs. This involves the robot migrating from a state (such as disabled)
     * to another state (such as autonomous).
     * @param state     The new state the robot is in
     * @param oldState  The state the robot was in before the transition (may be null)
     */
    @Override
    public void transitionState(RobotState state, RobotState oldState) {
        switch (state) {
            case DISABLED:
                disabledInit();
                break;
            case AUTONOMOUS:
                autonomousInit();
                break;
            case TELEOP:
                teleopInit();
                break;
            case TEST:
                testInit();
                break;
        }
    }

    /**
     * Called on 'Pre-Initialization' of the robot. This is called before the Robot is indicated as 'ready to go'. Inputs
     * and Outputs should be configured here. This method should not have much over-head
     */
    @Override
    public void prestart() { }

    /**
     * Called on 'Initialization' of the robot. This is called after the Robot is indicated as 'ready to go'. Things like
     * Network Communications and Camera Tracking should be initialized here.
     */
    @Override
    public void start() {
        robotInit();
    }

    /**
     * Get the Previous State the robot was in before the *Init() method.
     */
    public RobotState getPreviousState() {
        return StateTracker.lastState;
    }

    /**
     * Called when the Robot has started. This should be overridden.
     */
    public void robotInit() { }

    /**
     * Called when the Robot has entered Disabled mode. This should be overridden.
     */
    public void disabledInit() { }

    /**
     * Called when the Robot has entered Autonomous mode. This should be overridden.
     */
    public void autonomousInit() { }

    /**
     * Called when the Robot has entered Teleoperated mode. This should be overridden.
     */
    public void teleopInit() { }

    /**
     * Called when the Robot has entered Test mode. This should be overridden.
     */
    public void testInit() { }

    /**
     * Called when the Robot has ticked. This happens once every 20ms or once every control
     * packet, whatever comes first. This is for Disabled mode.
     */
    public void disabledPeriodic() { }

    /**
     * Called when the Robot has ticked. This happens once every 20ms or once every control
     * packet, whatever comes first. This is for Autonomous mode.
     */
    public void autonomousPeriodic() { }

    /**
     * Called when the Robot has ticked. This happens once every 20ms or once every control
     * packet, whatever comes first. This is for Teleoperated mode.
     */
    public void teleopPeriodic() { }

    /**
     * Called when the Robot has ticked. This happens once every 20ms or once every control
     * packet, whatever comes first. This is for Test mode.
     */
    public void testPeriodic() { }
}
