package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import jaci.openrio.toast.lib.FRCHooks;
import jaci.openrio.toast.lib.state.RobotState;
import jaci.openrio.toast.lib.state.StateListener;

import java.util.ArrayList;
import java.util.List;

import static jaci.openrio.toast.lib.state.RobotState.*;

/**
 * Keeps track of the {@link jaci.openrio.toast.lib.state.RobotState} the robot is in, as well as the one
 * it just switched from. This allows for context-aware state management.
 *
 * This class also allows classes to implement sub-interfaces of {@link jaci.openrio.toast.lib.state.StateListener},
 * which will trigger the interfaces when the robot 'ticks' or transitions between states. This allows for multiple
 * handlers to work with states
 *
 * @author Jaci
 */
public class StateTracker {

    static boolean _state_disabled_init = false;
    static boolean _state_autonomous_init = false;
    static boolean _state_teleop_init = false;
    static boolean _state_test_init = false;

    public static RobotState currentState;
    public static RobotState lastState;

    private static Toast impl;

    private static volatile List<StateListener.Ticker> tickers = new ArrayList<StateListener.Ticker>();
    private static volatile List<StateListener.Transition> transitioners = new ArrayList<StateListener.Transition>();

    /**
     * Start the StateTracker loop
     */
    public static void init(Toast impl) {
        UsageReporting.report(FRCNetworkCommunicationsLibrary.tResourceType.kResourceType_Framework, FRCNetworkCommunicationsLibrary.tInstances.kFramework_Iterative);
        StateTracker.impl = impl;
        boolean isAlive;
        while (true) {
            if (impl.isDisabled()) {
                if (!_state_disabled_init) {
                    transition(DISABLED);

                    _state_disabled_init = true;
                    _state_autonomous_init = false;
                    _state_teleop_init = false;
                    _state_test_init = false;
                }

                if (nextPeriodReady()) {
                    FRCHooks.observeDisabled();
                    tick(RobotState.DISABLED);
                }
            } else if (impl.isAutonomous()) {
                if (!_state_autonomous_init) {
                    transition(AUTONOMOUS);

                    _state_autonomous_init = true;
                    _state_disabled_init = false;
                    _state_teleop_init = false;
                    _state_test_init = false;
                }

                if (nextPeriodReady()) {
                    FRCHooks.observeAutonomous();
                    tick(RobotState.AUTONOMOUS);
                }
            } else if (impl.isTest()) {
                if (!_state_test_init) {
                    transition(TEST);

                    _state_test_init = true;
                    _state_disabled_init = false;
                    _state_autonomous_init = false;
                    _state_teleop_init = false;
                }

                if (nextPeriodReady()) {
                    FRCHooks.observeTest();
                    tick(RobotState.TEST);
                }
            } else {                //Teleop
                if (!_state_teleop_init) {
                    transition(TELEOP);

                    _state_teleop_init = true;
                    _state_disabled_init = false;
                    _state_autonomous_init = false;
                    _state_test_init = false;
                }

                if (nextPeriodReady()) {
                    FRCHooks.observeTeleop();
                    tick(RobotState.TELEOP);
                }
            }

            impl.station().waitForData();
        }
    }

    /**
     * Transition between the old state and the new (given) state
     */
    static void transition(RobotState state) {
        lastState = currentState;
        currentState = state;

        for (StateListener.Transition tra : transitioners)
            tra.transitionState(currentState, lastState);
    }

    /**
     * Tick all interfaces with the given state
     */
    public static void tick(RobotState state) {
        for (StateListener.Ticker ticker : tickers)
            ticker.tickState(state);
    }

    /**
     * Register a new 'Ticking' {@link jaci.openrio.toast.lib.state.StateListener}. This will tick
     * whenever a state has an update, or every 20ms. This is similar to the {@link edu.wpi.first.wpilibj.IterativeRobot}
     * implementation
     */
    public static void addTicker(StateListener.Ticker ticker) {
        tickers.add(ticker);
    }

    /**
     * Register a new 'Transition' {@link jaci.openrio.toast.lib.state.StateListener}. This will
     * trigger whenever the robot switches between states.
     */
    public static void addTransition(StateListener.Transition transition) {
        transitioners.add(transition);
    }

    private static boolean nextPeriodReady() {
        return impl.station().isNewControlData();
    }

}
