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

public class StateTracker {

    static boolean _state_disabled_init = false;
    static boolean _state_autonomous_init = false;
    static boolean _state_teleop_init = false;
    static boolean _state_test_init = false;

    public static RobotState currentState;
    public static RobotState lastState;

    private static Toast impl;

    private static List<StateListener.Ticker> tickers = new ArrayList<StateListener.Ticker>();
    private static List<StateListener.Transition> transitioners = new ArrayList<StateListener.Transition>();

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
        if (state == RobotState.TEST)                   //Set the LiveWindow state
            LiveWindow.setEnabled(true);
        else
            LiveWindow.setEnabled(false);

        lastState = currentState;
        currentState = state;

        for (StateListener.Transition tra : transitioners)
            tra.transitionState(currentState, lastState);
    }

    public static void tick(RobotState state) {
        for (StateListener.Ticker ticker : tickers)
            ticker.tickState(state);
    }

    public static void addTicker(StateListener.Ticker ticker) {
        tickers.add(ticker);
    }

    public static void addTransition(StateListener.Transition transition) {
        transitioners.add(transition);
    }

    private static boolean nextPeriodReady() {
        return impl.station().isNewControlData();
    }

}
