package jaci.openrio.toast.core.thread;

import jaci.openrio.toast.core.ToastConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Toast Thread Pool object is used to schedule multiple concurrent tasks across a predefined number of Threads.
 * The tasks happen some time in the future, but when that 'some time' is, we do not know. As such, this class should only
 * be used by functions that do not require a specified order or 'race condition'.
 *
 * The Thread Pool is already created for you, and can be accessed through ToastThreadPool.INSTANCE. From here, the only
 * method you should call to schedule a task is 'addWorker', taking a Runnable as an argument. This Runnable can be anonymous,
 * which is it's intended implementation.
 *
 * By default, the standard Thread count is 2, but can be changed either by creating a new ToastThreadPool object, or by
 * changing the 'threading.pool_size' variable in the Toast.groovy configuration file.
 *
 * @author Jaci
 */
public class ToastThreadPool {

    /**
     * The instance of the default ToastThreadPool. You should access this instead of creating your own instance
     * unless you /really/ need it.
     */
    public static ToastThreadPool INSTANCE;

    ExecutorService threadPool;

    /**
     * Create a new Thread Pool with the given name. The size of this Thread Pool is defined in the Toast.groovy
     * configuration file
     */
    public ToastThreadPool(String name) {
        threadPool = Executors.newFixedThreadPool(ToastConfiguration.Property.THREAD_POOL_SIZE.asInt(), new Factory(name));
    }

    /**
     * Create a new Thread Pool with the given name, as well as the 'fixed' size of the Thread pool. This should usually
     * be 2. If you are unsure of the size, use the {@link #ToastThreadPool(String)} constructor instead.
     */
    public ToastThreadPool(String name, int size) {
        threadPool = Executors.newFixedThreadPool(size, new Factory(name));
    }

    /**
     * Initialize the Thread Pool. This is handled by Toast, do not call this yourself.
     */
    public static void init() {
        INSTANCE = new ToastThreadPool("Pool-Worker");
    }

    /**
     * Add a new Worker to the Thread Pool. For basic usage, this is the only function you should call. This will schedule
     * a task to run when the Thread is not busy (add it to the queue).
      * @param runnable The Runnable Instance to trigger when running. This can be an anonymous member, which is the intended
     *                  implementation, although you can extend this interface and it will work just as well.
     */
    public void addWorker(Runnable runnable) {
        threadPool.execute(runnable);
    }

    /**
     * Shutdown the thread pool and wait for any pending workers to finish. DO NOT CALL THIS ON THE MAIN THREAD POOL.
     */
    public void finish() {
        threadPool.shutdown();
    }

    /**
     * Get the Thread Pool {@link java.util.concurrent.ExecutorService} for advanced implementations.
     */
    public ExecutorService getService() {
        return threadPool;
    }

    /**
     * Wait for all tasks on the Thread Pool to complete processing. This is called after {@link #finish} and as such
     * should not be called on the main thread pool.
     */
    public void waitForCompletion() {
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (Exception e) {}
    }

    private static class Factory implements ThreadFactory {

        private AtomicInteger integer = new AtomicInteger(1);

        String name;

        public Factory(String name) {
            this.name = name;
        }

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
