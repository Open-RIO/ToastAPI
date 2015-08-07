package jaci.openrio.toast.lib.log;

import java.io.IOException;
import java.io.PrintStream;

/**
 * An instance of a PrintStream that helps escape ANSI color control characters for a SplitStream. Color codes are just ignored for FileOutputs,
 * while on System.out they retain their color. This makes sure you don't get weird characters in your files.
 *
 * @author Jaci
 */
public class ColorPrint extends PrintStream {

    SplitStream spl;

    public ColorPrint(SplitStream out) {
        super(out);
        spl = out;
    }

    /**
     * Print a string. Escapes ANSI characters.
     */
    public void print(String s) {
        try {
            spl.color(s);
        } catch (IOException e) { }
    }

    public static interface ColorStream {}

}
