package toast.examples;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.lib.module.ToastStateModule;
import jaci.openrio.toast.lib.state.RobotState;

public class StateModule extends ToastStateModule {

  @Override
  public String getModuleName() {
      return "State_Toast_Module";
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
  }

  @Override
  public void tickState(RobotState state) {
    Toast.log().info("Ticking Periodic: " + state)      //Ticks every 20ms (or every control packet) with the 'state' {Autonomous, Teleoperated, Disabled, Test}
    if (state == RobotState.AUTONOMOUS) {
      // Your Autonomous Code
    }
  }

  @Override
  public void transitionState(RobotState state, RobotState oldState) {
    Toast.log().info("I went from " + state + " to " + oldState)      //Called when we move from 1 state to another {Autonomous, Teleoperated, Disabled, Test}
  }

}
