package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ToastModule;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import static jaci.openrio.toast.core.loader.ModuleManager.*;

public class RobotLoader {

    static Logger log;

    static String[] discoveryDirs = new String[]{"toast/modules/", "toast/system/modules/"};

    public static Pattern classFile = Pattern.compile("([^\\s$]+).class$");

    public static void init() {
        log = new Logger("Toast|ModuleLoader", Logger.ATTR_DEFAULT);

        loadCandidates();
        parseEntries();
        construct();
    }

    private static void loadCandidates() {
        for (String currentDirectory : discoveryDirs) {
            File dir = new File(currentDirectory);
                dir.mkdirs();
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
                        ModuleCandidate container = new ModuleCandidate();
                        container.setFile(file);
                        for (ZipEntry ze : Collections.list(jar.entries())) {
                            if (classFile.matcher(ze.getName()).matches()) {
                                container.addClassEntry(ze.getName());
                            }
                        }
                        candidates.add(container);
                        addURL(file.toURI().toURL());
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
                    try {
                        addURL(file.toURI().toURL());
                    } catch (Exception e) {}
                }
        }
    }

    private static void parseEntries() {
        for (ModuleCandidate candidate : candidates) {
            for (String clazz : candidate.getClassEntries()) {
                try {
                    Class c = Class.forName(clazz);
                    if (ToastModule.class.isAssignableFrom(c)) {
                        ModuleContainer container = new ModuleContainer(c, candidate);
                        containers.add(container);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private static void construct() {
        for (ModuleContainer container : containers) {
            try {
                container.construct();
                log.info("Module Loaded: " + container.getDetails());
            } catch (Exception e) {}
        }
    }

    public static void prestart() {
        for (ModuleContainer container : getContainers())
            container.getModule().prestart();
    }

    public static void start() {
        for (ModuleContainer container : getContainers())
            container.getModule().start();
    }

    public static void addURL(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, u);
        } catch (Throwable t) {
        }

    }

}