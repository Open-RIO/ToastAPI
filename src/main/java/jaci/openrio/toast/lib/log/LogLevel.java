package jaci.openrio.toast.lib.log;

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

    public LogLevel(String name) {
        SysLogProxy.init();
        this.name = name;
        stream = System.out;
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
