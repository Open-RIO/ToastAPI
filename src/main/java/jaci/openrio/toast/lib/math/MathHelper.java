package jaci.openrio.toast.lib.math;

import java.math.BigDecimal;

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
        BigDecimal bd = new BigDecimal(d).setScale(res, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Clamp an angle (in degrees) to 360, and transform negative angles into their positive counterparts.
     */
    public static double clampAngle(double angle) {
        double newAngle = angle % 360;
        if (newAngle < 0) newAngle = 360 + newAngle;
        return newAngle;
    }
}
