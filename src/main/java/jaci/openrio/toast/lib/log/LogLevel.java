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
    PrintStream stream = System.out;

    public LogLevel(String name) {
        this.name = name;
    }

    public LogLevel setName(String n) {
        this.name = n;
        return this;
    }

    public String getName() {
        return name;
    }

    public LogLevel setPrintStream(PrintStream stream) {
        this.stream = stream;
        return this;
    }

    public PrintStream getPrintSteam() {
        return stream;
    }

}
