package jaci.openrio.toast.lib.log;

import java.io.PrintStream;

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
