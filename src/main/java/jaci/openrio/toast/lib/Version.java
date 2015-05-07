package jaci.openrio.toast.lib;

import jaci.openrio.toast.core.ToastBootstrap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    static String version = "1.3.0";
    static Version vers;
    static boolean known = false;
    static Pattern versionPattern = Pattern.compile("(\\d*).(\\d*).(\\d*)(-(\\d*)([a-z]))?");

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

    public static boolean versionKnown() {
        return known;
    }

    public static Version version() {
        return vers;
    }

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

    public Version(String versionString) {
        versString = versionString;
        parsed = false;
        parse();
    }

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

    public String get() {
        if (!preRelease)
            return String.format("%s.%s.%s", major, minor, build);
        else
            return String.format("%s.%s.%s-%s%s", major, minor, build, prebuild, prereleaseType);
    }

    public String raw() {
        return versString;
    }

    public String toString() {
        if (!parsed)
            return "Version[unknown]";
        if (!preRelease)
            return String.format("Version[%s.%s.%s]", major, minor, build);
        else
            return String.format("Version[%s.%s.%s-%s%s]", major, minor, build, prebuild, prereleaseType);
    }

    /**
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

    public boolean newerThan(Version o) {
        return compareTo(o) == 1;
    }

    public boolean olderThan(Version o) {
        return compareTo(o) == -1;
    }

    public boolean equals(Version o) {
        return o.preRelease == preRelease && !(o.major != major || o.minor != minor || o.build != build) && (!preRelease || o.prereleaseType == prereleaseType && o.prebuild == prebuild);
    }

    public static class VersionFormatException extends RuntimeException {
    }

}
