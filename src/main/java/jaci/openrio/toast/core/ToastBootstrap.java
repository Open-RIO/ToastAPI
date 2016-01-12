package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.ClassPatcher;
import jaci.openrio.toast.core.loader.RobotLoader;
import jaci.openrio.toast.core.loader.simulation.DriverStationCommunications;
import jaci.openrio.toast.core.loader.simulation.SimulationGUI;
import jaci.openrio.toast.core.network.ToastSessionJoiner;
import jaci.openrio.toast.core.script.js.JavaScript;
import jaci.openrio.toast.core.security.ToastSecurityManager;
import jaci.openrio.toast.core.shared.GlobalBlackboard;
import jaci.openrio.toast.core.thread.Async;
import jaci.openrio.toast.lib.Assets;
import jaci.openrio.toast.lib.Version;
import jaci.openrio.toast.lib.crash.CrashHandler;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.log.SysLogProxy;
import jaci.openrio.toast.lib.module.ModuleConfig;
import jaci.openrio.toast.lib.profiler.Profiler;
import jaci.openrio.toast.lib.profiler.ProfilerSection;
import jaci.openrio.toast.lib.state.LoadPhase;

import java.io.File;
import java.io.IOException;
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

    public static boolean color = false;

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
    public static boolean isHeadless;
    public static boolean compareStub;

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
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 2);      // Slightly above normal priority, but below maximum priority.
        startTimeNS = System.nanoTime();
        startTimeMS = System.currentTimeMillis();
        ProfilerSection profiler = Profiler.INSTANCE.section("Setup");
        JavaScript.startLoading();
        profiler.start("ParseArgs");
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
            } else if (arg.equalsIgnoreCase("--color")) {
                color = true;
            } else if (arg.equalsIgnoreCase("-ide")) {
                if (args.length > (i+1))
                    if (args[i+1].equalsIgnoreCase("IDEA")) color = true;           // Most other IDEs don't support ANSI
            } else if (arg.equalsIgnoreCase("--join")) {
                ToastSessionJoiner.init();
                return;
            } else if (arg.equalsIgnoreCase("--headless")) {
                isHeadless = true;
            } else if (arg.equalsIgnoreCase("--stub")) {
                compareStub = true;
            }
        }
        profiler.stop("ParseArgs");

        if (compareStub) {
            System.out.println("Immediately exiting -- we've been told to exit before initialization for memory and utilization measurement purposes. Type something to end the program.");
            try {
                System.in.read();
            } catch (IOException e) {
                return;
            }
            return;
        }

        if (isSimulation) {
            toastHome = new File("toast/").getAbsoluteFile();
            robotHome = new File("./").getAbsoluteFile();
        }
        toastHome.mkdirs();

        LoadPhase.BOOTSTRAP.transition();

        profiler.start("Logger");
        SysLogProxy.init();
        profiler.stop("Logger");
        profiler.start("Crash");
        CrashHandler.init();
        profiler.stop("Crash");

        profiler.start("Misc");
        System.out.println(Assets.getAscii("splash"));
        toastLogger = new Logger("Toast", Logger.ATTR_DEFAULT);
        new GlobalBlackboard();
        GlobalBlackboard.INSTANCE.put("runtime_args", args);
        profiler.stop("Misc");

        try {
            Thread.sleep(20);       // To avoid splash screen flush racing
        } catch (InterruptedException e) { }

        profiler.start("Version");
        Version.init();
        profiler.stop("Version");

        // -------- NEW PHASE -------- //
        LoadPhase.CORE_PREINIT.transition();
        RobotLoader.preinit(Profiler.INSTANCE.section("RobotLoader"));

        // -------- NEW PHASE -------- //
        LoadPhase.CORE_INIT.transition();
        RobotLoader.initCore(Profiler.INSTANCE.section("RobotLoader"));

        if (args.length > 0)
            toastLogger.info("Toast Started with Run Arguments: " + Arrays.toString(args));

        ModuleConfig.init();

        // -------- NEW PHASE -------- //
        LoadPhase.PRE_INIT.transition();
        toastLogger.info("Slicing Loaf...");
        USBMassStorage.init();
        profiler.start("Configuration");
        ToastConfiguration.init();
        profiler.stop("Configuration");
        profiler.start("ThreadPool");
        Async.init();
        profiler.stop("ThreadPool");

        ClassPatcher classLoader = new ClassPatcher();
        classLoader.identifyPatches(isSimulation);

        if (isSimulation && !isVerification && !isHeadless) {
            SimulationGUI.main(args);
            DriverStationCommunications.init();
        }

        // -------- NEW PHASE -------- //
        LoadPhase.INIT.transition();
        toastLogger.info("Nuking Toast...");
        RobotLoader.postCore(Profiler.INSTANCE.section("RobotLoader"));

        profiler.start("Security");
        ToastSecurityManager.init();
        profiler.stop("Security");

        profiler.start("WPILib");
        RobotBase.main(args);
    }

}