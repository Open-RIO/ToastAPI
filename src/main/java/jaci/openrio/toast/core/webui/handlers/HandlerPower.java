package jaci.openrio.toast.core.webui.handlers;

import edu.wpi.first.wpilibj.ControllerPower;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.monitoring.power.PDPMonitor;
import jaci.openrio.toast.core.webui.WebHandler;
import jaci.openrio.toast.lib.math.MathHelper;

import java.util.HashMap;

/**
 * The web handler for the Power Distribution Panel. This implementation allows users to see information
 * about the Power Distribution Panel and current draw of its ports
 *
 * @author Jaci
 */
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
                double val = rnd(PDPMonitor.panel().getCurrent(n));
                return String.valueOf(val / 10) + ":" + val + "A";
            }

            switch (id) {
                case "batVolt":
                    double bv = rnd(PDPMonitor.panel().getVoltage());
                    return String.valueOf((bv - 11D) / 1.8D) + ":" + bv + "V";
                case "rioVolt":
                    double rv = rnd(ControllerPower.getInputVoltage());
                    return String.valueOf((rv - 11D) / 1.8D) + ":" + rv + "V";
                case "pdpTemp":
                    double t = rnd(PDPMonitor.panel().getTemperature());
                    return String.valueOf(t / 40D) + ":" + t + "C";
            }
        } catch (Exception e) {
            Toast.log().exception(e);
        }
        return "0";
    }

    double rnd(double x) {
        return MathHelper.round(x, 2);
    }

}
