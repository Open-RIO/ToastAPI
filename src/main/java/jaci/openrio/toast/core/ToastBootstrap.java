package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.core.loader.ClassPatcher;
import jaci.openrio.toast.core.loader.RobotLoader;
import jaci.openrio.toast.core.loader.simulation.SimulationGUI;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.log.SysLogProxy;

import java.io.File;

/**
 * The Bootstrap class for launching Toast before WPILib. This makes simulation, class patching, crash handling
 * and file logging possible. WPILib's main class is called after this class has finished it's configuration
 *
 * @author Jaci
 */
public class ToastBootstrap {

    /**
     * Get the Logger for Toast
     */
    public static Logger toastLogger;

    /**
     * Is this a simulation? This is set to true if the Launch Args include
     * -sim or -simulation
     */
    public static boolean isSimulation;

    /**
     * Get the root folder for Toast. This is where logs, modules,
     * and just about everything for toast is saved.
     */
    public static File toastHome = new File("/home/lvuser/toast/");
    public static File robotHome = new File("/home/lvuser/");

    public static long startTimeNS;
    public static long startTimeMS;

    public static void main(String[] args) {
        Thread.currentThread().setName("Toast-Bootstrap");
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("-simulation") || arg.equals("-sim")) {
                isSimulation = true;
                try {
                    String modClass = args[i + 1];
                    RobotLoader.manualLoadedClasses.add(modClass);
                } catch (Exception e) { }
            }
        }

        if (isSimulation) {
            toastHome = new File("toast/");
            robotHome = new File("./");
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