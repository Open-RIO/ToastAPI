package jaci.openrio.toast.core.thread;

public interface AsyncTask extends Runnable {

    default public void onAddedToQueue(Async pool) {}

    default public void run() {
        runTask();
        done();
    };

    public void runTask();

    default public void done() {};
}
