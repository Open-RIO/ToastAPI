package jaci.openrio.toast.lib.log;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * A logging class for the robot. This class prefixes different attributes to the message, such as time,
 * thread, and logger ID. This makes logs easy to read and debug.
 *
 * {@link jaci.openrio.toast.lib.log.LogHandler} can also be used in order to detect when a message is logged,
 * and do the appropriate action. This allows teams running Custom Driver Station software to view the console
 * in real time
 *
 * @author Jaci
 */
public class Logger {

    public static final int ATTR_TIME = 1;
    public static final int ATTR_THREAD = 2;

    public static final int ATTR_DEFAULT = ATTR_TIME | ATTR_THREAD;

    public static final LogLevel INFO = new LogLevel("INFO");
    public static final LogLevel WARN = new LogLevel("WARN").setPrintStream(System.err);
    public static final LogLevel ERROR = new LogLevel("ERROR").setPrintStream(System.err);
    public static final LogLevel SEVERE = new LogLevel("SEVERE").setPrintStream(System.err);

    public DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy-hh:mm:ss");

    public static Vector<String> backlog = new Vector<String>();
    public static Vector<LogHandler> handlers = new Vector<LogHandler>();

    int attr;
    String name;

    /**
     * Registers a {@link jaci.openrio.toast.lib.log.LogHandler} with all loggers
     */
    public static void addHandler(LogHandler handler) {
        handlers.add(handler);
    }

    /**
     * Create a new Logger Object
     * @param name The name of the logger
     * @param attributes A Binary Or operation of the attributes to use with the logger. This should be
     *                   ATTR_TIME | ATTR_THREAD, or ATTR_DEFAULT by shorthand, unless you want to omit this
     *                   data
     */
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

    /**
     * Log a message at the specified level
     */
    public void log(String message, LogLevel level) {
        log(message, level.getName().toUpperCase(), level.getPrintSteam());
    }

    /**
     * Log an 'INFO' message. This is the default for logging things
     * that are not errors.
     *
     * This goes to System.out
     */
    public void info(String message) {
        log(message, INFO);
    }

    /**
     * Log a 'WARNING' message. This is for errors that aren't critical
     * or too big of an issue, or if you hurt the robot's feelings
     *
     * This goes to System.err
     */
    public void warn(String message) {
        log(message, WARN);
    }

    /**
     * Log an 'ERROR' message. This is the default for errors that affect
     * robot action.
     *
     * This goes to System.err
     */
    public void error(String message) {
        log(message, ERROR);
    }

    /**
     * Log a 'SEVERE' message. This is for errors that deeply affect robot
     * action and are critical
     *
     * This goes to System.err
     */
    public void severe(String message) {
        log(message, SEVERE);
    }
}
