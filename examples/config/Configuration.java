package toast.examples;

import jaci.openrio.toast.lib.module.ModuleConfig;

public class Configuration {

    ModuleConfig preferences;

    public void register() {                // This would be called from your Main module
        preferences = new ModuleConfig("my_module_name");
        int my_int = preferences.getInt("my_int", 0);
        String my_str = preferences.getString("my_str", "Hello World");

        int nested_int = preferences.getInt("nested.my_int", 1);
    }

}
