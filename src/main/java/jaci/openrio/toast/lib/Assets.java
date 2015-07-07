package jaci.openrio.toast.lib;

import jaci.openrio.toast.lib.util.Pretty;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Assets {

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
