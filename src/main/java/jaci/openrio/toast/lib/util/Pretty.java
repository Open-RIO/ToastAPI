package jaci.openrio.toast.lib.util;

import jaci.openrio.toast.core.ToastBootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The simple, ANSI escape character-based Pretty Print engine. This allows for console messages to be colorized with any
 * valid ANSI colour code.
 *
 * This is usually done by formatting a string with HTML-style tags "&lt;&gt;", containing the name of the colour (i.e. &lt;red&gt;
 * or &lt;green&gt;). Everything BEFORE a tag will be colorized with that colour until it reaches another tag. To format
 * this for valid printing to a console, run Pretty.format(String) on your message. The return value can be printed to console.
 *
 * @author Jaci
 */
public class Pretty {

    /**
     * Colorize a string with the given ANSI values. In most cases, it's better off if you use
     * {@link #colorize(String, Colors)}
     */
    public static String colorize(String s, int code, int multiplier) {
        if (!ToastBootstrap.color) return s;
        return "\033[" + code + "m" + s + "\033[" + multiplier + "m";
    }

    /**
     * Colorize the string with the given {@link jaci.openrio.toast.lib.util.Pretty.Colors} object.
     */
    public static String colorize(String s, Colors c) {
        return colorize(s, c.code, c.multi);
    }

    static Pattern colorPattern = Pattern.compile("<([a-zA-Z_]*)>");

    /**
     * Format the string by looking for Color-Tags and replacing them with the
     * appropriate colour codes as described in the class javadoc
     */
    public static String format(String s) {
        List<String> color_repl = new ArrayList<>();
        Matcher m = colorPattern.matcher(s);
        while (m.find()) color_repl.add(m.group(1));

        String[] split = s.split(colorPattern.pattern());
        String total = "";

        for (int i = 0; i < split.length; i++) {
            if (i >= color_repl.size()) total += split[i];
            else {
                total += colorize(split[i], Colors.get(color_repl.get(i)));
            }
        }
        return total;
    }

    /**
     * Strip a string of its ANSI formatting. This removes colour codes. If you're storing a string to a file,
     * it's likely you want to store it after calling this method to ensure you don't store a mangled mess of
     * ASCII escape codes
     */
    public static String strip(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    /**
     * The enumeration of possible colours to use with the formatter. The names of the Enums are the same
     * that should be passed through to the Pretty.format() Color-Tags.
     */
    public static enum Colors {
        NORMAL          (0,0),
        BLACK           (30,0),
        RED             (31,0),
        GREEN           (32,0),
        BROWN           (33,0),
        BLUE            (34,0),
        MAGENTA         (35,0),
        CYAN            (36,0),
        GRAY            (37,0),

        BG_BLACK        (40,0),
        BG_RED          (41,0),
        BG_GREEN        (42,0),
        BG_BROWN        (43,0),
        BG_BLUE         (44,0),
        BG_MAGENTA      (45,0),
        BG_CYAN         (46,0),
        BG_GRAY         (47,0),

        BOLD            (1, 22),
        REVERSE         (7, 27),
        ;

        int code, multi;

        Colors(int code, int multiplier) {
            this.code = code; this.multi = multiplier;
        }

        /**
         * Get a color with the given name. These are automatically coerced to lowercase
         */
        public static Colors get(String name) {
            for (Colors col : values()) {
                if (col.name().toLowerCase().equals(name.toLowerCase()))
                    return col;
            }
            return Colors.GRAY;
        }
    }

}
