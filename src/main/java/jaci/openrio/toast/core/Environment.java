package jaci.openrio.toast.core;

import jaci.openrio.toast.core.script.js.JavaScript;
import jaci.openrio.toast.lib.Version;

import java.io.File;
import java.util.ArrayList;

/**
 * The Environment Class is used as a set of hooks to obtain data regarding the Environment the Robot is working in.
 * This is where modules should check for Verification/Simulated environments, check if FMS is attached, as well as
 * operated depending on the Operating System the Robot or Simulation is present in.
 *
 * @author Jaci
 */
public class Environment {

    /**
     * Is this a Verification Environment? A Verification Environment is launched with args --verify to test
     * the Robot's behaviour in a generic competition (15 sec Auto, 2 min Teleop). All state changes are done
     * in this mode, and any uncaught exceptions render the build as 'failed'
     */
    public static boolean isVerification() {
        return ToastBootstrap.isVerification;
    }

    /**
     * Is this a Simulation Environment? A Simulation Environment is launched with args --sim (--search) as a
     * means for Developers to test their code in their IDE without having a robot present. This is used very commonly,
     * and in most cases all the Class Patching is done for you, but special cases may require this hook.
     */
    public static boolean isSimulation() {
        return ToastBootstrap.isSimulation;
    }

    /**
     * Get the Toast home directory (in local storage) from the ToastBootstrap class. This does NOT account for USB
     * Mass Storage.
     */
    public static File getHome() {
        return ToastBootstrap.toastHome;
    }

    /**
     * Is this an Embedded Environment? Embedded environments are simply described as 'non-simulation' environments, and
     * are therefore assumed to be running on the NI RoboRIO or standard FRC competition computer onboard the Robot. The
     * only time a robot program is embedded is when it has been deployed to a robot and is running.
     */
    public static boolean isEmbedded() {
        return !isSimulation();
    }

    /**
     * Is this a competition? This returns true if the Robot and Driver Station have adequate communication to the
     * Field Management System (FMS). This is NOT a substitute for {@link #isEmbedded()}, as the program can be
     * both embedded and not connected to FMS during times when an FRC regulation field is not present.
     */
    public static boolean isCompetition() {
        return Toast.getToast().station().isFMSAttached();
    }

    /**
     * Get the Environmental Type in String form. This formats {@link #isSimulation()}, {@link #isVerification()} and
     * {@link #isEmbedded()} into their String form for writing to a console or other human-readable resources.
     */
    public static String getEnvironmentalType() {
        if (isVerification())
            return "Verification";
        else if (isSimulation())
            return "Simulation";
        else if (isEmbedded())
            return "Embedded";
        return "Unknown";
    }

    /**
     * Get the architecture of the Operating System. This usually contains x86, 32 or 64 bit systems depending on the
     * OS.
     */
    public static String getOS_Architecture() {
        return System.getProperty("os.arch");
    }

    /**
     * Get the OS Name. This returns things such as 'Linux', 'Mac OS X' or 'Windows'. Best practise is to use .contains()
     * on the return value, as Windows contains the version number (7, 8, 8.1, 10) in this String and uses os.version
     * as the build ID.
     */
    public static String getOS_Name() {
        return System.getProperty("os.name");
    }

    /**
     * Get the Operating System version. On Mac, this is the OS version such as 10.10, while on Windows this is the OS
     * build number such as 6.3. This changes per system, so it is most useful in Crash Logs and debugging.
     */
    public static String getOS_Version() {
        return System.getProperty("os.version");
    }

    /**
     * Get the Java Vendor. This is usually 'Oracle Corporation' or something similar, unless the user is using some
     * bootleg version of Java they got from the shady guy in the street called 'Big Moe'
     */
    public static String getJava_vendor() {
        return System.getProperty("java.vendor");
    }

    /**
     * Get the Operating System as an Enumeration. This is for when you want if statements without checking for String
     * constants or regex. In most cases, 'unknown' can be assumed to be Linux or Unix systems.
     */
    public static OS getOS() {
        String nm = getOS_Name();
        for (OS os : OS.values()) {
            if (os.equals(nm))
                return os;
        }
        return OS.UNKNOWN;
    }

    /**
     * Get the Java Version. This follows the standard '1.8.0_33' system. This should be prefixed with '1.8.x_', otherwise
     * chances are Java is out of date and Toast won't work properly. Update 25 or above is usually required for proper
     * usage.
     */
    public static String getJava_version() {
        return System.getProperty("java.version");
    }

    /**
     * Get the Java Home Directory. This is the location of the JRE and JDK. This is used to keep track of System .jar
     * files present on the system to make sure we don't load them for Module candidacy.
     */
    public static String getJava_home() {
        return System.getProperty("java.home");
    }

    /**
     * Get a String List of all the Environment Details, nicely formatted for Command Output or Crash Logs.
     */
    public static ArrayList<String> toLines() {
        ArrayList<String> list = new ArrayList<>();
        list.add(String.format("%10s %s", "Toast:", Version.version().get()));
        list.add(String.format("%10s %s (%s)", "Git:", Version.getShortCommitHash(), Version.getCommitHash()));
        list.add(String.format("%10s %s", "Type:", getEnvironmentalType()));
        list.add(String.format("%10s %s", "FMS:", isCompetition()));
        list.add(String.format("%10s %s %s (%s)", "OS:", getOS_Name(), getOS_Version(), getOS_Architecture()));
        list.add(String.format("%10s %s (%s)", "Java:", getJava_version(), getJava_vendor()));
        list.add(String.format("%10s %s", "Java Path:", getJava_home()));
        if (JavaScript.supported())
            list.add(String.format("%10s %s", "JScript:", "Supported (" + JavaScript.engineType() + ")"));
        else
            list.add(String.format("%10s %s", "JScript:", "Unsupported"));
        return list;
    }

    /**
     * Get all the environment details as a single String with newlines.
     */
    public static String asString() {
        return String.join("\n", toLines());
    }

    public static enum OS {
        WINDOWS("Win", false),
        MAC("Mac", true),
        LINUX("Nux", true),
        UNKNOWN("", false);

        String m;
        boolean u;

        OS(String match, boolean isUnix) {
            u = isUnix;
            m = match;
        }

        /**
         * Return true if the OS is a Unix system. In most cases, the union of this and the UNKNOWN key will
         * count towards Unix or Linux systems.
         */
        public boolean isUnix() {
            return u;
        }

        /**
         * Get the matching string for this enum. If the OS name contains this string, it will be detected as that OS.
         */
        public String getMatcher() {
            return m;
        }

        /**
         * Checks if the given OS string is equal to this enum. This is used in {@link #getOS()} to check the operating system
         * type.
         */
        public boolean equals(String s) {
            return s.toLowerCase().contains(m.toLowerCase());
        }

    }

}
