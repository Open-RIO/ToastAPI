package toast.examples;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.thread.Async;
import jaci.openrio.toast.lib.module.ToastModule;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ThreadWorker extends ToastModule {
    @Override
    public String getModuleName() {
        return "Heartbeat_Worker";
    }

    @Override
    public String getModuleVersion() {
        return "1.0.0";         // Recommended to follow the standard Toast Versioning System (major.minor.build-prebuild)
    }

    @Override
    public void prestart() {

    }

    @Override
    public void start() {
        Async.INSTANCE.addWorker(new Runnable() {                     // Add a new, void return type method to the Thread Pool
            @Override
            public void run() {
                Toast.log().info("Hello from the threadpool!");
            }
        });

        Future<Integer> future = Async.INSTANCE.addWorker(new Callable<Integer>() {       // Add a new, non-void return type method to the Thread Pool
            @Override
            public Integer call() throws Exception {
                return 5 * 2;
            }
        });

        Toast.log().info("Thread complete! Result: " + future.get());                              // => 10 (blocks/waits until result is complete)
    }
}
