package jaci.openrio.toast.lib.module;

import edu.wpi.first.wpilibj.Timer;
import jaci.openrio.toast.core.StateTracker;
import jaci.openrio.toast.lib.state.RobotState;
import jaci.openrio.toast.lib.state.StateListener;

/**
 * Provides an interface similar to {@link edu.wpi.first.wpilibj.IterativeRobot} for Toast Modules to
 * extend.
 *
 * @author Jaci
 */
public abstract class ToastIterativeModule extends ToastStateModule {


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

    private RobotState lastState = RobotState.DISABLED;

    @Override
    public void transitionState(RobotState state, RobotState oldState) {
        lastState = oldState;
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

    @Override
    public void prestart() {

    }

    @Override
    public void start() {
        robotInit();
    }

    public RobotState getPreviousState() {
        return lastState;
    }

    public void robotInit() { }

    public void disabledInit() { }

    public void autonomousInit() { }

    public void teleopInit() { }

    public void testInit() { }

    public void disabledPeriodic() { }

    public void autonomousPeriodic() { }

    public void teleopPeriodic() { }

    public void testPeriodic() { }
}
