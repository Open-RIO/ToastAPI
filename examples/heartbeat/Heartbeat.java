package toast.examples;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.thread.Heartbeat;
import jaci.openrio.toast.core.thread.HeartbeatListener;
import jaci.openrio.toast.lib.module.ToastModule;

public class HeartbeatModule extends ToastModule {

    @Override
    public String getModuleName() {
        return "Heartbeat_Toast_Module";
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
        Heartbeat.add(new HeartbeatListener() {     // Adds a listener to the Heartbeat
            @Override
            public void onHeartbeat(int skipped) {
                Toast.log().info("Duh-Dunk, Duh-Dunk");
            }
        });

        Heartbeat.add(new MyOtherClass());          // You can also do this for classes implementing 'Heartbeat Listener;
    }

    public static class MyOtherClass implements HeartbeatListener {

        @Override
        public void onHeartbeat(int skipped) {
            Toast.log().info("Other Duh-Dunk, other Duh-Dunk");
        }
    }

}
