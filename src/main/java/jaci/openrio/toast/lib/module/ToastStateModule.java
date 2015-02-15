package jaci.openrio.toast.lib.module;

import jaci.openrio.toast.core.StateTracker;
import jaci.openrio.toast.lib.state.StateListener;

public abstract class ToastStateModule extends ToastModule implements StateListener.Ticker, StateListener.Transition {

    public void onConstruct() {
        StateTracker.addTicker(this);
        StateTracker.addTransition(this);
    }

}
