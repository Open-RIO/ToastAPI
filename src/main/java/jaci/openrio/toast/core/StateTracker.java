package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import jaci.openrio.toast.core.loader.groovy.GroovyLoader;
import jaci.openrio.toast.core.loader.verification.VerificationWorker;
import jaci.openrio.toast.lib.FRCHooks;
import jaci.openrio.toast.lib.state.ConcurrentVector;
import jaci.openrio.toast.lib.state.RobotState;
import jaci.openrio.toast.lib.state.StateListener;

import static jaci.openrio.toast.lib.state.RobotState.*;

/**
 * Keeps track of the {@link jaci.openrio.toast.lib.state.RobotState} the robot is in, as well as the one
 * it just switched from. This allows for context-aware state management.
 * <p>
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

    private static volatile ConcurrentVector<StateListener.Ticker> tickers = new ConcurrentVector<StateListener.Ticker>();
    private static volatile ConcurrentVector<StateListener.Transition> transitioners = new ConcurrentVector<StateListener.Transition>();

    /**
     * Start the StateTracker loop
     */
    public static void init(Toast impl) {
        UsageReporting.report(FRCNetworkCommunicationsLibrary.tResourceType.kResourceType_Framework, FRCNetworkCommunicationsLibrary.tInstances.kFramework_Iterative);
        StateTracker.impl = impl;
        boolean isAlive;
        if (ToastBootstrap.isVerification)
            VerificationWorker.begin();
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

        transitioners.tick();

        for (StateListener.Transition tra : transitioners)
            tra.transitionState(currentState, lastState);

        GroovyLoader.transition(currentState);
    }

    /**
     * Tick all interfaces with the given state
     */
    public static void tick(RobotState state) {
        tickers.tick();

        for (StateListener.Ticker ticker : tickers)
            ticker.tickState(state);

        GroovyLoader.tick(state);
    }

    /**
     * Register a new 'Ticking' {@link jaci.openrio.toast.lib.state.StateListener}. This will tick
     * whenever a state has an update, or every 20ms. This is similar to the {@link edu.wpi.first.wpilibj.IterativeRobot}
     * implementation
     */
    public static void addTicker(StateListener.Ticker ticker) {
        tickers.addConcurrent(ticker);
    }

    /**
     * Register a new 'Transition' {@link jaci.openrio.toast.lib.state.StateListener}. This will
     * trigger whenever the robot switches between states.
     */
    public static void addTransition(StateListener.Transition transition) {
        transitioners.addConcurrent(transition);
    }

    /**
     * Remove a {@link jaci.openrio.toast.lib.state.StateListener.Ticker} from the
     * StateTracker
     */
    public static void removeTicker(StateListener.Ticker ticker) {
        tickers.removeConcurrent(ticker);
    }

    /**
     * Remove a {@link jaci.openrio.toast.lib.state.StateListener.Transition} from the
     * StateTracker
     */
    public static void removeTransition(StateListener.Transition transition) {
        transitioners.removeConcurrent(transition);
    }

    private static boolean nextPeriodReady() {
        return impl.station().isNewControlData();
    }

}
