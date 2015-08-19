package jaci.openrio.toast.core.thread;

import jaci.openrio.toast.core.ToastConfiguration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Async extends ThreadPoolExecutor {

    public static Async INSTANCE;

    public static void init() {
        int size = ToastConfiguration.Property.THREAD_POOL_SIZE.asInt();
        INSTANCE = new Async(size, size, "Toast|Async");
    }

    public Async(int corePoolSize, int maximumPoolSize, String name) {
        super(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new Factory(name));
    }

    public Future submit(AsyncTask task) {
        Future f = submit((Runnable)task);
        task.onAddedToQueue(this);
        return f;
    }

    public static Future schedule(AsyncTask task) {
        return INSTANCE.submit(task);
    }

    /**
     * Shutdown the thread pool and wait for any pending workers to finish. DO NOT CALL THIS ON THE MAIN THREAD POOL.
     */
    public void finish() {
        shutdown();
    }

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
