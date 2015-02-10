package jaci.openrio.toast.core.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class RobotClassLoader extends URLClassLoader {

    public RobotClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public void addURL(URL url) {
        super.addURL(url);
    }

}
