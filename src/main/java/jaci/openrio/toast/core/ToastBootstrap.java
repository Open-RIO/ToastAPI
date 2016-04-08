package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.ClassPatcher;
import jaci.openrio.toast.core.loader.RobotLoader;
import jaci.openrio.toast.core.loader.simulation.DriverStationCommunications;
import jaci.openrio.toast.core.loader.simulation.SimulationGUI;
import jaci.openrio.toast.core.network.ToastSessionJoiner;
import jaci.openrio.toast.core.script.js.JavaScript;
import jaci.openrio.toast.core.security.ToastSecurityManager;
import jaci.openrio.toast.core.shared.GlobalBlackboard;
import jaci.openrio.toast.core.shared.OptionParser;
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
import jaci.openrio.toast.lib.state.RobotState;

import java.io.File;
import java.io.FileOutputStream;
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

    public static boolean debug_logging = false;
    public static boolean exception_info_logging = false;

    /**
     * Get the root folder for Toast. This is where logs, modules,
     * and just about everything for toast is saved.
     */
    public static File toastHome = new File("/home/lvuser/toast/");
    public static File robotHome = new File("/home/lvuser/");

    public static long startTimeNS;
    public static long startTimeMS;
    public static long endTimeMS;

    public static Thread main_thread;
    public static Thread watch_init_thread;

    /**
     * The Main Method. Called when the Toast Program is started. This contains multiple arguments, such as
     * -sim, -verify, -groovy, -groovyClass, -core and any others that are required. More of these are outlined in
     * the Whitepaper and various Loaders. Modules may also add their own arguments by retrieving 'runtime_args' from
     * the GlobalBlackboard
     */
    public static void main(String[] args) {
        main_thread = Thread.currentThread();
        watch_init_thread = new Thread(() -> {
            try {
                Thread.sleep(2 * 60 * 1000);
                if (LoadPhase.currentPhase != LoadPhase.COMPLETE) {
                    System.err.println("[ ERROR ] Toast is taking over 2 minutes to initiate. This is not normal. Following is " +
                            "a full detailed list of where each thread is in its execution. If you are seeing this message, " +
                            "please notify the developers on the Toast API Main Repository (http://github.com/Open-RIO/ToastAPI");
                    System.err.println("\tMAIN Thread:");
                    for (StackTraceElement element : main_thread.getStackTrace()) {
                        System.err.println("\t\tat " + element);
                    }

                    Thread.getAllStackTraces().forEach((thread, stackTraceElements) -> {
                        System.err.println("\tThread: '" + thread.getName() + "' priority: " + thread.getPriority() + " group: '" + thread.getThreadGroup().getName() + "'");
                        for (StackTraceElement element : stackTraceElements) {
                            System.err.println("\t\tat " + element);
                        }
                    });
                }
            } catch (InterruptedException e) { }
        });
        watch_init_thread.start();

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 2);      // Slightly above normal priority, but below maximum priority.
        startTimeNS = System.nanoTime();
        startTimeMS = System.currentTimeMillis();
        ProfilerSection profiler = Profiler.INSTANCE.section("Setup");
        JavaScript.startLoading();
        profiler.start("ParseArgs");
        OptionParser parser = new OptionParser(true);

        parser.on("-sim", "-simulation", "Start a Simulation with the following class. Use --search to search for Module Classes, or '.' to just start a Simulation without a class", 1, list -> {
            isSimulation = true;
            try {
                String component = list.get(0);
                if (!component.equals(".")) {
                    if (component.equalsIgnoreCase("--search")) {
                        RobotLoader.search = true;
                    } else RobotLoader.manualLoadedClasses.add(component);
                }
            } catch (Exception e) { }
        });

        parser.on("-vf", "-verify", "Start a Headless Verification", 0, list -> {
            isSimulation = true;
            isVerification = true;
        });

        parser.on("-core", "Load a Core Module class", 1, list -> {
            try {
                String component = list.get(0);
                if (!component.equals(".")) {
                    RobotLoader.coreClasses.add(component);
                    RobotLoader.manualLoadedClasses.add(component);
                }
            } catch (Exception e) { }
        });

        parser.on("--color", "Enable Color Output", 0, list -> { color = true; });

        parser.on("-ide", "Set the IDE you are currently using. This will be either ECLIPSE or IDEA, usually.", 1, list -> {
            try {
                String component = list.get(0);
                if (component.equalsIgnoreCase("IDEA")) color = true;
            } catch (Exception e) {  }
        });

        parser.on("-j", "--join", "Join a currently running Toast Logging session", 0, list -> {
            ToastSessionJoiner.init();
            System.exit(0);
        });

        parser.on("-nogui", "--headless", "Run the Simulation Headless (without a GUI)", 0, list -> {
            isHeadless = true;
        });

        parser.on("--stub", "Exit the program immediately. This is mostly used to profile memory usage over time.", 0, list -> {
            compareStub = true;
        });

        parser.on("-d", "--debug", "Launch in Debug Mode. More logging output and exception printing to aid in debugging issues.", 0, list -> {
            debug_logging = true;
            exception_info_logging = true;
        });

        parser.on("--MOAR", "Launch in Debug Mode. More logging output and exception printing to aid in debugging issues.", 0, list -> {
            debug_logging = true;
            exception_info_logging = true;
        });

        parser.on("-e", "--exceptions", "Debug Mode for Runtime Exceptions. More exception printing to aid in debugging issues", 0, list -> {
            exception_info_logging = true;
        });

        parser.parse(args);
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
//        RobotBase.main(args);
        fakeRobotBase();
    }

    // For some stupid reason WPILib decides to read EVERY manifest in the classpath and not bother to check if Robot-Class
    // is null before assigning it. To fix this, we have to 'fake' RobotBase. This bug is in the WPILib bug tracker.
    public static void fakeRobotBase() {
        RobotBase.initializeHardwareConfiguration();
        UsageReporting.report(FRCNetworkCommunicationsLibrary.tResourceType.kResourceType_Language, FRCNetworkCommunicationsLibrary.tInstances.kLanguage_Java);
        Toast toast = new Toast();

        if (!isSimulation) {
            File file = null;
            FileOutputStream output = null;
            try {
                file = new File("/tmp/frc_versions/FRC_Lib_Version.ini");

                if (file.exists())
                    file.delete();

                file.createNewFile();

                output = new FileOutputStream(file);

                output.write("2016 Java Beta5.0".getBytes());

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }

        toast.startCompetition();
    }

}