package toast.examples;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.lib.module.ToastModule;
import jaci.openrio.toast.lib.state.RobotState;
import jaci.openrio.toast.lib.state.StateListener;

public class StateInterfaces extends ToastModule {

  @Override
  public String getModuleName() {
      return "State_Interfaces_Module";
  }

  @Override
  public String getModuleVersion() {
      return "1.0.0";         // Recommended to follow the standard Toast Versioning System (major.minor.build-prebuild)
  }

  @Override
  public void prestart() {
    StateTracker.addTicker(new MyTickingClass())      // This can link to any class implementing StateListener.Ticker
  }

  @Override
  public void start() {

  }

  public static class MyTickingClass implements StateListener.Ticker {      // This can be in a new file, but we keep it in the same file for organisation's sake
    @Override
    public void tickState(RobotState state) {
      Toast.log().info("Hello from my other ticking class!")
    }
  }

}
