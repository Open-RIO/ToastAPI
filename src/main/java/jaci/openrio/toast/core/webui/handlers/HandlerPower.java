package jaci.openrio.toast.core.webui.handlers;

import edu.wpi.first.wpilibj.ControllerPower;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.monitoring.power.PDPMonitor;
import jaci.openrio.toast.core.webui.WebHandler;

import java.util.HashMap;

public class HandlerPower implements WebHandler {

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
        return url.contains("api/power.html");
    }

    @Override
    public String doOverride(String url, HashMap<String, String> post, HashMap<String, String> get, HashMap<String, String> headers) {
        try {
            String id = get.get("id");

            if (id.startsWith("current")) {
                int n = Integer.parseInt(id.replace("current", ""));
                return String.valueOf(PDPMonitor.panel().getCurrent(n) / 10);
            }

            switch (id) {
                case "batVolt":
                    return String.valueOf((PDPMonitor.panel().getVoltage() - 11D) / 1.8D);
                case "rioVolt":
                    return String.valueOf((ControllerPower.getInputVoltage() - 11D) / 1.8D);
                case "pdpTemp":
                    return String.valueOf(PDPMonitor.panel().getTemperature() / 40D);
            }
        } catch (Exception e) {
            Toast.log().exception(e);
        }
        return "0";
    }

}
