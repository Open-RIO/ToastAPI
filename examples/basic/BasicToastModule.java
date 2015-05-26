package toast.examples;

import jaci.openrio.toast.lib.module.ToastModule;
import jaci.openrio.toast.core.Toast;

public class BasicToastModule extends ToastModule {

  @Override
  public String getModuleName() {
      return "Basic_Toast_Module";
  }

  @Override
  public String getModuleVersion() {
      return "1.0.0";         // Recommended to follow the standard Toast Versioning System (major.minor.build-prebuild)
  }

  @Override
  public void prestart() {
    Toast.log().info("Hi from Module!");
  }

  @Override
  public void start() {
  }

}
