package jaci.openrio.toast.core.webui.handlers;

import edu.wpi.first.wpilibj.ControllerPower;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.monitoring.power.PDPMonitor;
import jaci.openrio.toast.core.webui.WebHandler;
import jaci.openrio.toast.lib.log.Logger;

import java.util.HashMap;

/**
 * The web handler for Console Logging. This allows users to see the console output via the Web UI
 *
 * @author Jaci
 */
public class HandlerConsole implements WebHandler {

    @Override
    public boolean handleURL(String url) {
        return false;
    }

    @Override
    public String handle(String url, HashMap<String, String> post, HashMap<String, String> get, HashMap<String, String> headers) {
        return null;
    }

    @Override
    public boolean overrideData(String url, HashMap<String, String> post, HashMap<String, String> get, HashMap<String, String> headers) {
        return url.contains("console/console.html");
    }

    @Override
    public String doOverride(String url, HashMap<String, String> post, HashMap<String, String> get, HashMap<String, String> headers) {
        try {
            if (url.contains("console/console.html")) {
                String s = "";
                for (String st : Logger.backlog)
                    s += st;
                return s;
            }
        } catch (Exception e) {
            Toast.log().exception(e);
        }
        return "0";
    }

}
