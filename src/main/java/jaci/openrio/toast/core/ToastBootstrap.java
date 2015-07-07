package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.ClassPatcher;
import jaci.openrio.toast.core.loader.RobotLoader;
import jaci.openrio.toast.core.loader.simulation.SimulationGUI;
import jaci.openrio.toast.core.script.js.JavaScript;
import jaci.openrio.toast.core.security.ToastSecurityManager;
import jaci.openrio.toast.core.shared.GlobalBlackboard;
import jaci.openrio.toast.core.thread.ToastThreadPool;
import jaci.openrio.toast.lib.Assets;
import jaci.openrio.toast.lib.Version;
import jaci.openrio.toast.lib.crash.CrashHandler;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.log.SysLogProxy;
import jaci.openrio.toast.lib.module.ModuleConfig;
import jaci.openrio.toast.lib.state.LoadPhase;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

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
    public static long endTimeMS;

    /**
     * The Main Method. Called when the Toast Program is started. This contains multiple arguments, such as
     * -sim, -verify, -groovy, -groovyClass, -core and any others that are required. More of these are outlined in
     * the Whitepaper and various Loaders. Modules may also add their own arguments by retrieving 'runtime_args' from
     * the GlobalBlackboard
     */
    public static void main(String[] args) {
        startTimeNS = System.nanoTime();
        startTimeMS = System.currentTimeMillis();
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
                    if (!nextArg.equals(".")) {
                        if (nextArg.equalsIgnoreCase("--search")) {
                            RobotLoader.search = true;
                        } else {
                            RobotLoader.manualLoadedClasses.add(nextArg);
                        }
                    }
                } catch (Exception e) { }
            } else if (arg.equalsIgnoreCase("-verify") || arg.equalsIgnoreCase("-vf")) {
                isSimulation = true;
                isVerification = true;
            } else if (arg.equalsIgnoreCase("-core")) {
                try {
                    if (!nextArg.equals(".")) {
                        RobotLoader.coreClasses.add(nextArg);
                        RobotLoader.manualLoadedClasses.add(nextArg);
                    }
                } catch (Exception e) { }
            }
        }

        if (isSimulation) {
            toastHome = new File("toast/").getAbsoluteFile();
            robotHome = new File("./").getAbsoluteFile();
        }
        toastHome.mkdirs();

        JavaScript.init();
        ModuleConfig.init();

        SysLogProxy.init();
        CrashHandler.init();

        System.out.println(Assets.getAscii("splash"));
        toastLogger = new Logger("Toast", Logger.ATTR_DEFAULT);
        new GlobalBlackboard();
        GlobalBlackboard.INSTANCE.put("runtime_args", args);

        Version.init();
        ToastSecurityManager.init();

        // -------- NEW PHASE -------- //
        LoadPhase.CORE_PREINIT.transition();
        RobotLoader.preinit();

        // -------- NEW PHASE -------- //
        LoadPhase.CORE_INIT.transition();
        RobotLoader.initCore();

        if (args.length > 0)
            toastLogger.info("Toast Started with Run Arguments: " + Arrays.toString(args));

        // -------- NEW PHASE -------- //
        LoadPhase.PRE_INIT.transition();
        toastLogger.info("Slicing Loaf...");
        USBMassStorage.init();
        ToastConfiguration.init();
        ToastThreadPool.init();

        ClassPatcher classLoader = new ClassPatcher();
        classLoader.identifyPatches(isSimulation);

        if (isSimulation && !isVerification) {
            SimulationGUI.main(args);
        }

        // -------- NEW PHASE -------- //
        LoadPhase.INIT.transition();
        toastLogger.info("Nuking Toast...");
        RobotLoader.postCore();
        JavaScript.binderInit();
        RobotBase.main(args);
    }

}