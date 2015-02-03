package jaci.openrio.toast.lib.log;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logger {

    public static final int ATTR_TIME = 1;
    public static final int ATTR_THREAD = 2;

    public static final int ATTR_DEFAULT = ATTR_TIME | ATTR_THREAD;

    public static final LogLevel INFO = new LogLevel("INFO");
    public static final LogLevel WARN = new LogLevel("WARN").setPrintStream(System.err);
    public static final LogLevel ERROR = new LogLevel("ERROR").setPrintStream(System.err);
    public static final LogLevel SEVERE = new LogLevel("SEVERE").setPrintStream(System.err);

    public DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy-hh:mm:ss");

    public static ArrayList<String> backlog = new ArrayList<String>();
    public static ArrayList<LogHandler> handlers = new ArrayList<LogHandler>();

    int attr;
    String name;

    public static void addHandler(LogHandler handler) {
        handlers.add(handler);
    }

    public Logger(String name, int attributes) {
        this.attr = attributes;
        this.name = name;
    }

    String getTime() {
        return dateFormat.format(new Date());
    }

    /* LOGGING BEGIN */
    private void log(String message, String level, PrintStream ps) {
        StringBuilder builder = new StringBuilder();

        builder.append(getPrefix(level));

        builder.append(message);

        String ts = builder.toString();

        ps.println(ts);

        backlog.add(ts);

        for (LogHandler hand : handlers)
            hand.onLog(level, message);
    }

    public String getPrefix(String level) {
        StringBuilder builder = new StringBuilder();

        if ((attr & ATTR_TIME) == ATTR_TIME)
            builder.append("[" + getTime() + "] ");

        builder.append("[" + name + "] ");

        if ((attr & ATTR_THREAD) == ATTR_THREAD)
            builder.append("[" + Thread.currentThread().getName() + "] ");

        builder.append("[" + level + "] ");

        return builder.toString();
    }

    public void exception(Throwable e) {
        String s = "";
        s += e.toString() + "\n";
        for (StackTraceElement element : e.getStackTrace())
            s += "\tat " + element + "\n";

        error(s);
    }

    public void log(String message, LogLevel level) {
        log(message, level.getName().toUpperCase(), level.getPrintSteam());
    }

    public void info(String message) {
        log(message, INFO);
    }

    public void warn(String message) {
        log(message, WARN);
    }

    public void error(String message) {
        log(message, ERROR);
    }

    public void severe(String message) {
        log(message, SEVERE);
    }
}
