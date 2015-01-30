package jaci.openrio.toast.lib.math;

public class MathHelper {

    public static double round(double d, int res) {
        int x = (int) Math.pow(10, res);
        return Math.floor(d * x) / x;
    }

}
