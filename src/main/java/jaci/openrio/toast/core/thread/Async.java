package jaci.openrio.toast.core.thread;

import jaci.openrio.toast.core.ToastConfiguration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Toast Asynchronous Thread Pool. This class acts as a task-scheduling system, in which tasks can be submitted for
 * work and will be completed when available. By default, the Thread Pool will launch 4 tasks at a time, and when each is
 * complete it will schedule the next. This can be changed in the Toast.conf file.
 *
 * Async Tasks have no guarantee for when they complete, and so this should be used for time-insensitive tasks. If you
 * rely on getting a return value or something similar of a Task, a Future is provided so you can 'wait' for the task to
 * be scheduled and finished.
 *
 * @author Jaci
 */
public class Async extends ThreadPoolExecutor {

    public static Async INSTANCE;

    /**
     * Start the main Async INSTANCE. This is done for you in Toast initialization.
     */
    public static void init() {
        int size = ToastConfiguration.Property.THREAD_POOL_SIZE.asInt();
        INSTANCE = new Async(size, size, "Toast|Async");
    }

    /**
     * Create a new Async pool for you own usage
     * @param corePoolSize      The starting size of the pool
     * @param maximumPoolSize   The maximum size of the pool
     * @param name              The name of the pool
     */
    public Async(int corePoolSize, int maximumPoolSize, String name) {
        super(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new Factory(name));
    }

    /**
     * Submit a task to this Async pool. Calls the {@link AsyncTask#onAddedToQueue(Async, Future)} method
     */
    public Future submit(AsyncTask task) {
        Future f = submit((Runnable)task);
        task.onAddedToQueue(this, f);
        return f;
    }

    /**
     * Submit a task to the default Async pool. Calls the {@link AsyncTask#onAddedToQueue(Async, Future)} method
     */
    public static Future schedule(AsyncTask task) {
        return INSTANCE.submit(task);
    }

    /**
     * Shutdown the thread pool and wait for any pending workers to finish. DO NOT CALL THIS ON THE MAIN THREAD POOL.
     */
    public void finish() {
        shutdown();
    }

    /**
     * Wait until the threadpool is done with all work, forever. DO NOT CALL THIS ON THE MAIN THREAD POOL.
     */
    public void waitForCompletion() {
        try {
            awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (Exception e) {}
    }

    // Factory
    private static class Factory implements ThreadFactory {

        private AtomicInteger integer = new AtomicInteger(1);

        String name;

        public Factory(String name) {
            this.name = name;
        }

        /**
         * Creates a new Thread Object for the ThreadFactory. This is given the proper name for the Thread Pool and is not
         * set as a daemon to assure that it can be easily debugged in profilers and other utilities.
         */
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, name + "-" + integer.getAndIncrement());
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }

    }

}
