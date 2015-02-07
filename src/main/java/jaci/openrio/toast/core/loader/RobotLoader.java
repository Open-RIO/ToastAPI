package jaci.openrio.toast.core.loader;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.lib.crash.CrashHandler;
import jaci.openrio.toast.lib.Robot;
import jaci.openrio.toast.lib.log.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

/**
 * Utility class to load the class for the Robot's main class, as Toast acts as a middle-man
 *
 * @author Jaci
 */
public class RobotLoader {

    static Robot robot;
    static Logger log;

    public static void init() {
        log = new Logger("Toast|RobotLoader", Logger.ATTR_DEFAULT);

        String roboClass = "";
        Enumeration<URL> resources = null;
        try {
            resources = RobotBase.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                roboClass = manifest.getMainAttributes().getValue("Toast-Class");
            }
        } catch (IOException e) {
            log.error("Could not load Robot Manifest");
            log.exception(e);

            CrashHandler.handle(e);
        }

        try {
            robot = (Robot) Class.forName(roboClass).newInstance();
        } catch (Throwable t) {
            log.error("Could not load Robot Class");
            log.exception(t);

            CrashHandler.handle(t);
        }

    }

    public static Robot getRobot() {
        return robot;
    }

}
