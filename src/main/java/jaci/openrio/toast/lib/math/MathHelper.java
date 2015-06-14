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

    /**
     * Split a String by the specified amount of words. This is used in
     * Configurations to make sure the descriptions don't span across the
     * entire file.
     */
    public static String[] splitStringByWords(String string, int wordsPerLine) {
        String[] strings = string.split(" ");
        int last = strings.length % wordsPerLine;
        int keys = strings.length / wordsPerLine;
        if (last != 0) keys += 1;

        String[] lines = new String[keys];

        for (int i = 0; i < keys; i++) {
            int iteration = wordsPerLine;
            if (i == keys - 1 && last != 0)
                iteration = last;
            int offset = i * wordsPerLine;
            String line = "";
            for (int j = offset; j < offset + iteration; j++) {
                line += strings[j] + " ";
            }
            lines[i] = line;
        }
        return lines;
    }

}
