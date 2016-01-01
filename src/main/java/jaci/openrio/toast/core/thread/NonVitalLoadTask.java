package jaci.openrio.toast.core.thread;

/**
 * A Non-Vital Load Task is a task that starts initialization with Toast's Bootstrapper, but isn't necessarily
 * a requirement later on in the load process. These Load Tasks are started at Toast Bootstrap, and load in their
 * own Thread. From there on, the task will load and not cause any blocking on the main thread, reducing startup
 * time. If a module requires the use of the task, the Task will block the thread (join) and wait until loading
 * is complete before advancing. This method enables lengthy tasks (such as JavaScript engine preparation) to be
 * threaded and only be waited on if they are required for a task. If the loading is already complete, the thread
 * will not block and will be allowed to continue immediately.
 *
 * @author Jaci
 */
public class NonVitalLoadTask {

    private Thread __loading_thread;
    private Runnable __loading_runnable;
    private boolean __loaded = false;

    public NonVitalLoadTask(Runnable run) {
        this.__loading_runnable = run;
    }

    /**
     * Require the task to be completely loaded before continuing. This ensures that the task is prepared
     * and ready to go before use. This is a blocking method, but may return immediately if loading is already complete.
     */
    public void requireLoaded() {
        if (__loaded) return;
        if (__loading_thread == null)
            throw new IllegalStateException("Loading has not started yet!");
        try {
            __loading_thread.join();
            __loaded = true;
        } catch (InterruptedException e) {  }
    }

    /**
     * Start the loading of the task. This will launch a new thread dedicated to the loading of this task,
     * so any tasks being used should be classified as Thread-Safe.
     */
    public void startLoading() {
        __loading_thread = new Thread(__loading_runnable);
        __loading_thread.start();
        __loading_thread.setPriority(Thread.MIN_PRIORITY);
    }

}
