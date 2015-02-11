package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ToastModule;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class RobotLoader {

    static Logger log;

    static String[] discoveryDirs = new String[]{"toast/modules/", "toast/system/modules/"};
    static ArrayList<ToastModuleCandidate> candidates = new ArrayList<ToastModuleCandidate>();
    static ArrayList<ToastModuleContainer> containers = new ArrayList<ToastModuleContainer>();

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
                        ToastModuleCandidate container = new ToastModuleCandidate();
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
        for (ToastModuleCandidate candidate : candidates) {
            for (String clazz : candidate.classMembers) {
                try {
                    Class c = Class.forName(clazz);
                    if (ToastModule.class.isAssignableFrom(c)) {
                        ToastModuleContainer container = new ToastModuleContainer(c);
                        containers.add(container);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private static void construct() {
        for (ToastModuleContainer container : containers) {
            try {
                container.construct();
                log.info("Module Loaded: " + container.name + "@" + container.version);
            } catch (Exception e) {}
        }
    }

    public static List<ToastModuleContainer> getContainers() {
        return containers;
    }

    public static void prestart() {
        for (ToastModuleContainer container : getContainers())
            container.moduleInstance.prestart();
    }

    public static void start() {
        for (ToastModuleContainer container : getContainers())
            container.moduleInstance.start();
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