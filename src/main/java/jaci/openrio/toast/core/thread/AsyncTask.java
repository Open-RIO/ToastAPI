package jaci.openrio.toast.core.thread;

import java.util.concurrent.Future;

/**
 * The default interface for AsyncTasks to be submitted to {@link Async}. This can be created anonymously or can
 * have a dedicated class if you so desire.
 *
 * @author Jaci
 */
public interface AsyncTask extends Runnable {

    /**
     * Called when the task is submitted to the queue. By default this is left blank
     */
    default public void onAddedToQueue(Async pool, Future future) {}

    /**
     * Called when the task has been scheduled to run. Do not override this, instead use {@link #runTask()}
     */
    default public void run() {
        runTask();
        done();
    };

    /**
     * Called when the task has been scheduled to run.
     */
    public void runTask();

    /**
     * Called when the task has completed the {@link #runTask()} method. This is left blank by default.
     */
    default public void done() {};
}
