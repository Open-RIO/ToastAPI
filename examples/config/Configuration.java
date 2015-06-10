package toast.examples;

import jaci.openrio.toast.core.loader.groovy.GroovyPreferences;

public class Configuration {

    GroovyPreferences preferences;

    public void register() {                // This would be called from your Main module
        preferences = new GroovyPreferences("my_module_name");
        int my_int = preferences.getInt("my_int", 0, "A standard integer for the module");
        String my_str = preferences.getString("my_str", "Hello World", "A standard string for the module");

        int nested_int = preferences.getInt("nested.my_int", 1, "An integer nested inside of 'nested'");
    }

}
