package jaci.openrio.toast.lib;

import jaci.openrio.toast.lib.util.Pretty;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * The helper class for fetching Assets inside of the classpath
 *
 * @author Jaci
 */
public class Assets {

    /**
     * Get an ASCII resource. This is used for getting the Splash Screen as well as the 'uh oh' screen when the Robot crashes.
     */
    public static String getAscii(String name) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Assets.class.getResourceAsStream("/assets/toast/ascii/" + name + ".txt")))) {
            String ln;
            String total = "";
            while ((ln = reader.readLine()) != null) {
                total += ln + "\n";
            }
            return Pretty.format(total);
        } catch (Exception e) {
            return "";
        }
    }

}
