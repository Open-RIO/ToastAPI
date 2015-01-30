package jaci.openrio.toast.core.loader;

import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.lib.crash.CrashHandler;
import jaci.openrio.toast.lib.Robot;
import jaci.openrio.toast.lib.log.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class RobotLoader {

    static Robot robot;
    static Logger log;

    public static void init() {
        log = new Logger("Toast|RobotLoader", Logger.ATTR_DEFAULT);

        try {
            for (Class<?> clazz : Robot.class.getDeclaredClasses())
                if (clazz.getSuperclass().equals(Robot.class)) {
                    robot = (Robot) clazz.newInstance();
                    break;
                }
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
