package jaci.openrio.toast.lib.log;

import jaci.openrio.toast.lib.util.Pretty;

import java.io.PrintStream;

/**
 * A level used by the {@link jaci.openrio.toast.lib.log.Logger} object. This is used to
 * differentiate severities of logged data
 *
 * @author Jaci
 */
public class LogLevel {

    String name;
    PrintStream stream;
    Pretty.Colors color;

    public LogLevel(String name) {
        SysLogProxy.init();
        this.name = name;
        stream = System.out;
        color = Pretty.Colors.NORMAL;
    }

    /**
     * Set the name of the Log Level. This is seen in the Log message in upper case to define
     * the urgency of the message.
     */
    public LogLevel setName(String n) {
        this.name = n;
        return this;
    }

    /**
     * Set the color of the Logger. This is red for warnings
     */
    public LogLevel setColor(Pretty.Colors color) {
        this.color = color;
        return this;
    }

    public Pretty.Colors getColor() {
        return color;
    }

    /**
     * Get the name of the Log Level. This is used in the Log message to define the urgency of
     * the message.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the PrintStream of the Logger. This should really only be System.out or System.err if
     * you want the message to show up in the console or log files.
     */
    public LogLevel setPrintStream(PrintStream stream) {
        this.stream = stream;
        return this;
    }

    /**
     * Get the PrintStream object that was set for this Log Level. This is System.out by default.
     */
    public PrintStream getPrintSteam() {
        return stream;
    }

}
