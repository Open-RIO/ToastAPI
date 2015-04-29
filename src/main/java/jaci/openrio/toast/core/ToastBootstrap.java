package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.ClassPatcher;
import jaci.openrio.toast.core.loader.RobotLoader;
import jaci.openrio.toast.core.loader.groovy.GroovyLoader;
import jaci.openrio.toast.core.loader.groovy.GroovyPreferences;
import jaci.openrio.toast.core.loader.simulation.SimulationGUI;
import jaci.openrio.toast.core.shared.GlobalBlackboard;
import jaci.openrio.toast.core.thread.ToastThreadPool;
import jaci.openrio.toast.lib.crash.CrashHandler;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.log.SysLogProxy;
import jaci.openrio.toast.lib.state.LoadPhase;

import java.io.File;
import java.util.Arrays;

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
     * Is this a verification? Verifications are headless routines to 'test' the
     * build process.
     */
    public static boolean isVerification;

    /**
     * Get the root folder for Toast. This is where logs, modules,
     * and just about everything for toast is saved.
     */
    public static File toastHome = new File("/home/lvuser/toast/");
    public static File robotHome = new File("/home/lvuser/");

    public static long startTimeNS;
    public static long startTimeMS;

    public static void main(String[] args) {
        LoadPhase.BOOTSTRAP.transition();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String nextArg = null;
            try {
                nextArg = args[i+1];
            } catch (Exception e) {}
            if (arg.equalsIgnoreCase("-simulation") || arg.equals("-sim")) {
                isSimulation = true;
                try {
                    if (!nextArg.equals("."))
                        RobotLoader.manualLoadedClasses.add(nextArg);
                } catch (Exception e) { }
            } else if (arg.equalsIgnoreCase("-groovy") || arg.equalsIgnoreCase("-g")) {
                try {
                    if (!nextArg.equals("."))
                        GroovyLoader.customFiles.add(new File(nextArg));
                } catch (Exception e) {}
            } else if (arg.equalsIgnoreCase("-groovyClass") || arg.equalsIgnoreCase("-gc")) {
                try {
                    if (!nextArg.equals("."))
                        GroovyLoader.customClasses.add(nextArg);
                } catch (Exception e) {}
            } else if (arg.equalsIgnoreCase("-verify") || arg.equalsIgnoreCase("-vf")) {
                isSimulation = true;
                isVerification = true;
            } else if (arg.equalsIgnoreCase("-core")) {
                try {
                    if (!nextArg.equals("."))
                        RobotLoader.coreClasses.add(nextArg);
                } catch (Exception e) { }
            }
        }

        if (isSimulation) {
            toastHome = new File("toast/").getAbsoluteFile();
            robotHome = new File("./").getAbsoluteFile();
        }

        toastHome.mkdirs();

        SysLogProxy.init();
        CrashHandler.init();

        toastLogger = new Logger("Toast", Logger.ATTR_DEFAULT);
        new GlobalBlackboard();

        GroovyLoader.preinit();
        RobotLoader.preinit();
        if (GroovyLoader.coreScriptsLoaded)
            toastLogger.info("Groovy Core Scripts Loaded.");

        if (args.length > 0)
            toastLogger.info("Toast Started with Run Arguments: " + Arrays.toString(args));

        // -------- NEW PHASE -------- //
        LoadPhase.PRE_INIT.transition();
        toastLogger.info("Slicing Loaf...");
        GroovyPreferences.init();
        ToastConfiguration.init();
        ToastThreadPool.init();

        ClassPatcher classLoader = new ClassPatcher();
        classLoader.identifyPatches(isSimulation);

        startTimeNS = System.nanoTime();
        startTimeMS = System.currentTimeMillis();

        if (isSimulation && !isVerification) {
            SimulationGUI.main(args);
        }

        USBMassStorage.init();


        // -------- NEW PHASE -------- //
        LoadPhase.INIT.transition();
        toastLogger.info("Nuking Toast...");
        RobotBase.main(args);
    }

}