package jaci.openrio.toast.lib.math;

/**
 * A utility class to help with Math
 *
 * @author Jaci
 */
public class MathHelper {

    /**
     * Round a number (d) to the specified amount of decimal places (res)
     */
    public static double round(double d, int res) {
        int x = (int) Math.pow(10, res);
        return Math.rint(d * x) / x;
    }
}
