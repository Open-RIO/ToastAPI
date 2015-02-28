package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.ToastBootstrap;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * The class that handles runtime patching of classes. This allows for Simulations to happen
 * in development environments by patching WPILib's classes. This functions as a replacement for the System
 * Class Loader during Module Loading
 */
public class ClassPatcher extends URLClassLoader {

    public ClassPatcher() {
        this(ClassLoader.getSystemClassLoader());
    }

    public ClassPatcher(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        String simulation = "assets/toast/patches/" + name.replace(".", "/") + ".sim";
        String patch = "assets/toast/patches/" + name.replace(".", "/" + ".pat");
        InputStream stream = null;
        if (this.getResource(simulation) != null)
            stream = this.getResourceAsStream(simulation);
        if (this.getResource(patch) != null)
            stream = this.getResourceAsStream(patch);

        if (stream != null) {
            try {
                byte[] buf = new byte[10000];
                int len = stream.read(buf);

                Method m = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                m.setAccessible(true);
                m.invoke(getParent(), name, buf, 0, len);
            } catch (Exception e) {
                ToastBootstrap.toastLogger.error("Could not load Simulation Patch: " + name);
                ToastBootstrap.toastLogger.exception(e);
            }
        }

        return super.loadClass(name);
    }

    /**
     * Identify the patches from assets/toast/patches/patches.txt
     */
    public void identifyPatches(boolean sim) {
        try {
            ArrayList<String> patches = new ArrayList<>();
            InputStream is = getResourceAsStream("assets/toast/patches/patches.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = null;
            while ((line = reader.readLine()) != null) {
                patches.add(line);
            }

            reader.close();

            for (String s : patches) {
                try {
                    if (s.endsWith(".sim") && sim)
                        loadClass(s.replace(".sim","").replace("/", "."));
                    else if (s.endsWith(".pat"))
                        loadClass(s.replace(".pat","").replace("/", "."));
                } catch (Exception e) {
                    ToastBootstrap.toastLogger.error("Could not load Simulation Patch: " + s);
                    ToastBootstrap.toastLogger.exception(e);
                }
            }

        } catch (Exception e) {
            ToastBootstrap.toastLogger.error("Could not load patches. Ignoring...");
        }
    }
}
