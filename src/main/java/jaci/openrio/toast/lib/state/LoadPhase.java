package jaci.openrio.toast.lib.state;

import jaci.openrio.toast.lib.profiler.Profiler;

import java.util.Vector;
import java.util.function.Function;

/**
 * The different phases of the Toast lifecycle. These are all passed through before the robot is ready to start.
 *
 * This is really only for debugging purposes, but is still useful in some circumstances.
 *
 * @author Jaci
 */
public enum LoadPhase {
    /**
     * The Bootstrap Phase is where the Robot is first started. This handles parsing of command line arguments, as well
     * as setting up things such as the Logger, Global Blackboard, as well as creating directories.
     */
    BOOTSTRAP("Bootstrap"),

    /**
     * Called when the Core Modules are starting to be loaded into the classpath. This is between the absolute necessities
     * of Toast being loaded, and the Pre-WPILib State of Toast's Loading.
     */
    CORE_PREINIT("Core-Pre-Initialization"),

    /**
     * Called when all the Core Modules are loaded into the classpath, instantiated and are now doing their post-loading
     * process. All core modules will be loaded before this step, meaning Core Modules can now use their dependencies.
     */
    CORE_INIT("Core-Initialization"),

    /**
     * The Pre-Initialization phase is used to instantiate classes and objects. This is run before any external modules
     * or files are loaded so that no NullPointers are triggered when trying to access static variables
     */
    PRE_INIT("Pre-Initialization"),

    /**
     * The initialization Phase is used to load WPILib. Everything that happens in this phase is a result of WPILib's
     * {@link edu.wpi.first.wpilibj.RobotBase} class and is out of our control. Keep in mind during Simulation and
     * Verification, this class is Patched.
     */
    INIT("Initialization"),

    /**
     * Prestart is a standard phase of WPILib, and is where the robot is "getting ready". This is where modules are
     * loaded and things like the CommandBus instantiated.
     */
    PRE_START("Pre-Start"),

    /**
     * The Start Phase is another feature of WPILib. This is where the robot is seen as "Ready To Go". For our purposes,
     * we use this to start tools like the PDP Poll and SocketManagers.
     */
    START("Start"),

    /**
     * The Complete Phase is instantiated when all other phases are done. The robot is now fully ready to go.
     */
    COMPLETE("Main");

    String threadName;
    static LoadPhase currentPhase;

    LoadPhase(String threadName) {
        this.threadName = threadName;
    }

    /**
     * Get the localized, human-readable name of the LoadPhase.
     */
    public String getLocalized() {
        return threadName;
    }

    /**
     * Transition into the given load phase. This method changes the current Thread Name, sets the Current Phase and
     * updates any callbacks registered
     */
    public void transition() {
        Thread.currentThread().setName(threadName);
        if (currentPhase != null) {
            Profiler.INSTANCE.section("LoadPhase").stop(currentPhase.threadName);
        }
        currentPhase = this;
        if (currentPhase != COMPLETE) {
            Profiler.INSTANCE.section("LoadPhase").start(currentPhase.threadName);
        }
        for (Function<LoadPhase, Void> callback : callbacks)
            callback.apply(this);

    }

    /**
     * Register a callback on the LoadPhase callback handler system. A callback is called when the Robot undergoes a change
     * of LoadPhase. The intended use for this is with CoreModules, which may choose to use the LoadPhase system to trigger
     * Indicator LEDs, or certain methods where a method hook is not provided.
     */
    public static void addCallback(Function<LoadPhase, Void> callback) {
        callbacks.add(callback);
    }

    static Vector<Function<LoadPhase, Void>> callbacks = new Vector<>();
}
