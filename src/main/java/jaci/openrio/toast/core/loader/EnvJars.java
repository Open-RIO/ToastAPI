package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.Environment;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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

    public static void add(String pat) {
        knownFiles.add(Pattern.compile(pat));
    }

    public static boolean isLoadable(File f) {
        boolean jdk = isJDKJar(f);
        boolean known = isKnown(f);
        return !jdk && !known;
    }

    public static boolean isKnown(File f) {
        String name = f.getName().replace(".jar", "");
        for (Pattern p : knownFiles) {
            Matcher matcher = p.matcher(name);
            if (matcher.matches())
                return true;
        }
        return false;
    }

    public static boolean isJDKJar(File f) {
        String file = f.getAbsolutePath();
        String jdk = new File(Environment.getJava_home()).getAbsolutePath();
        return file.startsWith(jdk);
    }

}
