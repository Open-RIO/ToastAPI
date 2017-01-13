package jaci.openrio.toast.lib.log;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.util.Pretty;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    public static final int ATTR_COLOR = 4;

    public static final int ATTR_DEFAULT = ATTR_TIME | ATTR_THREAD | ATTR_COLOR;

    public static final LogLevel INFO = new LogLevel("INFO");
    public static final LogLevel DEBUG = new LogLevel("DEBUG");
    public static final LogLevel WARN = new LogLevel("WARN").setPrintStream(System.err).setColor(Pretty.Colors.RED);
    public static final LogLevel ERROR = new LogLevel("ERROR").setPrintStream(System.err).setColor(Pretty.Colors.RED);
    public static final LogLevel SEVERE = new LogLevel("SEVERE").setPrintStream(System.err).setColor(Pretty.Colors.RED);

    public DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy-hh:mm:ss");

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

    /**
     * Formats the current time data of the system into the [dd/MM/yy-hh:mm:ss] format
     */
    String getTime() {
        return dateFormat.format(new Date());
    }

    /* LOGGING BEGIN */

    /**
     * Log a new message on the selected printstream with the given method and level. This is where all other
     * 'log' type methods in this class delegate to.
     */
    private void log(String message, String level, String levelColor, PrintStream ps) {
        StringBuilder builder = new StringBuilder();

        if (!level.equals("raw"))
            builder.append(getPrefix(level, levelColor));

        builder.append(message);

        String ts = builder.toString();

        ps.println(ts);

        for (LogHandler hand : handlers)
            hand.onLog(level, message, ts, this);
    }

    /**
     * Get the prefix for all messages on this logger. This adds the DateTime, as well as the
     * Thread ID and Logger Name if they are enabled in the Logger Attributes
     */
    public String getPrefix(String level, String levelColor) {
        StringBuilder builder = new StringBuilder();

        if ((attr & ATTR_TIME) == ATTR_TIME)
            builder.append(tag(getTime(), "green", "gray"));

        builder.append(tag(name, "green", "magenta"));

        if ((attr & ATTR_THREAD) == ATTR_THREAD)
            builder.append(tag(Thread.currentThread().getName(), "green", "cyan"));

        builder.append(tag(level, "green", levelColor));

        return builder.toString();
    }

    /**
     * Return a tag, maybe formatted with color
     */
    public String tag(String inner, String c1, String c2) {
        boolean color = ((attr & ATTR_COLOR) == ATTR_COLOR) && ToastBootstrap.color;
        String s = "[";
        if (color) s += "<" + c1 + ">";
        s += inner;
        if (color) s += "<" + c2 + ">";
        s += "] ";
        if (color) s += "<" + c1 + ">";
        return color ? Pretty.format(s) : s;
    }

    /**
     * Log a message in Debug Mode (e.g. only if runtime arg)
     */
    public void debug(String message) {
        if (ToastBootstrap.debug_logging)
            log(message, DEBUG);
    }

    /**
     * Log an exception in Debug Mode (e.g. only if debug or exception runtime arg)
     */
    public void debugException(Throwable t) {
        if (ToastBootstrap.exception_info_logging || ToastBootstrap.debug_logging) {
            DEBUG.getPrintSteam().flush();
            log("DEBUG EXCEPTION: " + t, ERROR);
            exception(t);
            ERROR.getPrintSteam().flush();
        }
    }

    /**
     * Format and print a stack trace on the Logger Object. This calls the StackTrace directly, meaning
     * underlying causes and other factors are included in the log. This is the equivalent of calling
     * e.printStackTrace(), but redirecting it to the logger object.
     */
    public void exception(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        error(writer.toString());
    }

    /**
     * Log a message at the specified level
     */
    public void log(String message, LogLevel level) {
        log(message, level.getName().toUpperCase(), level.getColor().name().toLowerCase(), level.getPrintSteam());
    }

    /**
     * Log a message without any padding or prefixes.
     */
    public void raw(String message) {
        log(message, "raw", "none", System.out);
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
