package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.core.loader.ClassPatcher;
import jaci.openrio.toast.core.loader.simulation.SimulationGUI;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.log.SysLogProxy;

import java.io.File;

public class ToastBootstrap {

    public static Logger toastLogger;

    public static boolean isSimulation;

    public static File toastHome = new File("/home/lvuser/toast/");

    public static long startTimeNS;
    public static long startTimeMS;

    public static void main(String[] args) {
        Thread.currentThread().setName("Toast-Bootstrap");
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-simulation") || arg.equals("-sim")) {
                isSimulation = true;
            }
        }

        if (isSimulation) {
            toastHome = new File("toast/");
        }

        toastHome.mkdirs();

        SysLogProxy.init();
        toastLogger = new Logger("Toast", Logger.ATTR_DEFAULT);

        toastLogger.info("Slicing Loaf...");

        ClassPatcher classLoader = new ClassPatcher();
        classLoader.identifyPatches(isSimulation);

        startTimeNS = System.nanoTime();
        startTimeMS = System.currentTimeMillis();

        if (isSimulation) {
            SimulationGUI.main(args);
        }

        toastLogger.info("Nuking Toast...");
        RobotBase.main(args);
    }

}
