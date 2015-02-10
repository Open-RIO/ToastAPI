package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.IToastModule;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * Utility class to load the class for the Robot's main class, as Toast acts as a middle-man
 *
 * @author Jaci
 */
public class RobotLoader {

    static Logger log;

    static String[] discoveryDirs = new String[]{"toast/modules/", "toast/system/modules/"};
    static ArrayList<ToastModuleCandidate> candidates = new ArrayList<ToastModuleCandidate>();
    static ArrayList<ToastModuleContainer> containers = new ArrayList<ToastModuleContainer>();

    static RobotClassLoader loader;

    public static Pattern classFile = Pattern.compile("([^\\s$]+).class$");

    public static void init() throws IOException {
        log = new Logger("Toast|ModuleLoader", Logger.ATTR_DEFAULT);
        loader = new RobotClassLoader(RobotLoader.class.getClassLoader());

        loadCandidates();
        parseEntries();
    }

    private static void loadCandidates() throws IOException {
        for (String currentDirectory : discoveryDirs) {
            File dir = new File(currentDirectory);
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });

            if (files != null)
                for (File file : files) {
                    try {
                        JarFile jar = new JarFile(file);
                        ToastModuleCandidate container = new ToastModuleCandidate();
                        container.setFile(file);
                        for (ZipEntry ze : Collections.list(jar.entries())) {
                            if (classFile.matcher(ze.getName()).matches()) {
                                container.addClassEntry(ze.getName());
                            }
                        }
                        candidates.add(container);
                        loader.addURL(file.toURI().toURL());
                    } catch (Exception e) {
                    }
                }

            File[] otherFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !name.endsWith(".jar");
                }
            });

            if (otherFiles != null)
                for (File file : otherFiles) {
                    loader.addURL(file.toURI().toURL());
                }
        }
    }

    private static void parseEntries() {
        for (ToastModuleCandidate candidate : candidates) {
            for (String clazz : candidate.classMembers) {
                try {
                    Class c = loader.loadClass(clazz);
                    if (IToastModule.class.isAssignableFrom(c)) {
                        ToastModuleContainer container = new ToastModuleContainer(c);

                    }
                } catch (Exception e) {
                }
            }
        }
    }

}