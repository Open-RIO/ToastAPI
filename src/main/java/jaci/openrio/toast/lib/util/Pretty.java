package jaci.openrio.toast.lib.util;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import jaci.openrio.toast.core.ToastBootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pretty {

    public static String colorize(String s, int code, int multiplier) {
        if (!ToastBootstrap.color) return s;
        return "\033[" + code + "m" + s + "\033[" + multiplier + "m";
    }

    public static String colorize(String s, Colors c) {
        return colorize(s, c.code, c.multi);
    }

    static Pattern colorPattern = Pattern.compile("<([a-zA-Z_]*)>");

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

    public static String strip(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

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

        public static Colors get(String name) {
            for (Colors col : values()) {
                if (col.name().toLowerCase().equals(name.toLowerCase()))
                    return col;
            }
            return Colors.GRAY;
        }
    }

}
