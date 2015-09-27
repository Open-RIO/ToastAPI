package jaci.openrio.toast.core.thread;

public class NonVitalLoadTask {

    private Thread __loading_thread;
    private Runnable __loading_runnable;
    private boolean __loaded = false;

    public NonVitalLoadTask(Runnable run) {
        this.__loading_runnable = run;
    }

    public void requireLoaded() {
        if (__loaded) return;
        if (__loading_thread == null)
            throw new IllegalStateException("Loading has not started yet!");
        try {
            __loading_thread.join();
            __loaded = true;
        } catch (InterruptedException e) {  }
    }

    public void startLoading() {
        __loading_thread = new Thread(__loading_runnable);
        __loading_thread.start();
    }

}
