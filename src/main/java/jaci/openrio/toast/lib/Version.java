package jaci.openrio.toast.lib;

import jaci.openrio.toast.core.ToastBootstrap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The versioning manager for Toast. This allows for modules to easily parse what version of Toast is running
 * on the Robot.
 *
 * Keep in mind this isn't enforced in {@link jaci.openrio.toast.lib.module.ToastModule}, since some modules
 * may choose to respect an 'irregular' version formatting scheme
 *
 * @author Jaci
 */
public class Version implements Comparable<Version> {

    static String version = "1.3.0";
    static Version vers;
    static boolean known = false;
    static Pattern versionPattern = Pattern.compile("(\\d*).(\\d*).(\\d*)(-(\\d*)([a-z]))?");

    /**
     * Initialize the Version. This reads from the toast.version file in the jar file, parsing the Regex and validating the Toast
     * File's Version for modules to read.
     */
    public static void init() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("assets/toast/toast.version")));
            version = reader.readLine();
            reader.close();
            ToastBootstrap.toastLogger.info("Toast Version: " + version);
            known = true;
            vers = new Version(version);
        } catch (Exception e) {
            ToastBootstrap.toastLogger.error("Could not retrieve Toast Version, Using Default: (" + version + ")");
        }
    }

    /**
     * Returns true if we know what version Toast is, or false if we don't. This will only return false if
     * there was an error reading the /assets/toast/toast.version file.
     */
    public static boolean versionKnown() {
        return known;
    }

    /**
     * Get Toast's current version.
     */
    public static Version version() {
        return vers;
    }

    /**
     * Returns true if 'version' is equal to or newer than 'required'
     */
    public static boolean requireOrNewer(Version version, Version required) {
        int compare = version.compareTo(required);
        return compare == 0 || compare == 1;
    }

    // -- inst -- //
    String versString;

    int major;
    int minor;
    int build;
    boolean preRelease;
    char prereleaseType;
    int prebuild;

    boolean parsed;

    /**
     * Create a new Version and Parse it. The version object should be in the form
     * 'major.minor.build[-prebuild]', with everything in square brackets being
     * optional. 'prebuild' should consist of a build number following a letter
     * regarding the build type (A for Alpha, B for Beta, etc).
     *
     * e.g.
     * "1.3.0"
     * "1.3.0-10a"
     */
    public Version(String versionString) {
        versString = versionString;
        parsed = false;
        parse();
    }

    /**
     * Parse the Version String with the Regex pattern
     */
    public void parse() {
        Matcher matcher = versionPattern.matcher(versString);
        int c = matcher.groupCount();
        if (c < 3 || !matcher.matches()) throw new VersionFormatException();
        major = Integer.parseInt(matcher.group(1));
        minor = Integer.parseInt(matcher.group(2));
        build = Integer.parseInt(matcher.group(3));
        if (c == 6 && matcher.group(6) != null) {
            preRelease = true;
            prereleaseType = matcher.group(6).charAt(0);
            prebuild = Integer.parseInt(matcher.group(5));
        }
        parsed = true;
    }

    /**
     * Get the parsed version as a formatted string
     */
    public String get() {
        if (!preRelease)
            return String.format("%s.%s.%s", major, minor, build);
        else
            return String.format("%s.%s.%s-%s%s", major, minor, build, prebuild, prereleaseType);
    }

    /**
     * Get the raw version passed into the constructor (unparsed)
     */
    public String raw() {
        return versString;
    }

    /**
     * Convert the Version to String Format, for use in printing to the console or other implementations
     */
    public String toString() {
        if (!parsed)
            return "Version[unknown]";
        if (!preRelease)
            return String.format("Version[%s.%s.%s]", major, minor, build);
        else
            return String.format("Version[%s.%s.%s-%s%s]", major, minor, build, prebuild, prereleaseType);
    }

    /**
     * Compare 2 versions against each other.
     * @return 0 if versions are equal, -1 if older than 'o', 1 if newer than 'o'
     */
    @Override
    public int compareTo(Version o) {
        if (equals(o)) return 0;
        if (o.major != major)
            return o.major > major ? -1 : 1;
        if (o.minor != minor)
            return o.minor > minor ? -1 : 1;
        if (o.build != build)
            return o.build > build ? -1 : 1;
        if (o.preRelease && preRelease) {
            if (o.prebuild == prebuild)
                return o.prereleaseType > prereleaseType ? -1 : 1;
            return o.prebuild > prebuild ? -1 : 1;
        } else if (o.preRelease)
            return -1;
        else if (preRelease)
            return 1;
        return 0;
    }

    /**
     * Returns true if this version is newer than 'o'
     */
    public boolean newerThan(Version o) {
        return compareTo(o) == 1;
    }

    /**
     * Returns true if this version is older than 'o'
     */
    public boolean olderThan(Version o) {
        return compareTo(o) == -1;
    }

    /**
     * Returns true if versions are equal
     */
    public boolean equals(Version o) {
        return o.preRelease == preRelease && !(o.major != major || o.minor != minor || o.build != build) && (!preRelease || o.prereleaseType == prereleaseType && o.prebuild == prebuild);
    }

    /**
     * Thrown if a supplied version does not pass the Regex Matcher (is malformed).
     * @see {@link jaci.openrio.toast.lib.Version#Version(String)}
     */
    public static class VersionFormatException extends RuntimeException { }

}
