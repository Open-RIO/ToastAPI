package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helps Toast differentiate between what is a dependency and what isn't
 *
 * @author Jaci
 */
public class EnvJars {

    public static ArrayList<Pattern> knownFiles;

    static {
        knownFiles = new ArrayList<>();

        add("NetworkTables");
        add("WPILib");
        add("idea_rt");
        add("gragent");
        add("groovy-.*");
    }

    /**
     * Register a new Known file or path on the registry. Files or paths matching the Regex
     * pattern provided will not be loaded into the Toast Classpath or tested for Module
     * candidacy.
     */
    public static void add(String pat) {
        knownFiles.add(Pattern.compile(pat));
    }

    /**
     * Returns whether or not the file should be loaded or tested for candidacy.
     * @see #isKnown(File)
     * @see #isJDKJar(File)
     */
    public static boolean isLoadable(File f) {
        boolean jdk = isJDKJar(f);
        boolean known = isKnown(f);
        return !jdk && !known;
    }

    /**
     * Will return true if the given file is known, and therefore shouldn't be checked for module candidacy. This
     * is used for things like Toast's external libraries and system jars
     */
    public static boolean isKnown(File f) {
        String name = f.getName().replace(".jar", "");
        for (Pattern p : knownFiles) {
            Matcher matcher = p.matcher(name);
            if (matcher.matches())
                return true;
        }
        return false;
    }

    /**
     * Returns true if the given file is a JDK jar. This is gauged by whether or not the file's absolute path intersects
     * with the System's JDK home Environmental Variable.
     */
    public static boolean isJDKJar(File f) {
        String file = f.getAbsolutePath();
        String jdk = new File(Environment.getJava_home()).getAbsolutePath();
        return file.startsWith(jdk);
    }

}
