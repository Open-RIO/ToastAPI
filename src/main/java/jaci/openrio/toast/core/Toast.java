package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.core.command.CommandBus;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.core.loader.RobotLoader;
import jaci.openrio.toast.core.loader.groovy.GroovyLoader;
import jaci.openrio.toast.core.network.SocketManager;
import jaci.openrio.toast.core.thread.Heartbeat;
import jaci.openrio.toast.core.thread.HeartbeatListener;
import jaci.openrio.toast.core.thread.ToastThreadPool;
import jaci.openrio.toast.lib.FRCHooks;
import jaci.openrio.toast.lib.crash.CrashHandler;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.registry.MotorRegistry;
import jaci.openrio.toast.lib.state.LoadPhase;

import java.util.Random;

/**
 * The Toast Base Class. This is the base for the Toast API.
 *
 * @author Jaci
 */
public class Toast extends RobotBase {

    private static String[] tastes = new String[] {"Delicious", "Yummy", "Like a buttery heaven", "Needs more salt", "Hot, Hot, HOT!!", "TOTE-aly delicious"};

    private static Toast instance;

    public Toast() {
        super();
        instance = this;
    }

    /**
     * Get the instance of Toast. This is the instance that WPILib loads
     */
    public static Toast getToast() {
        return instance;
    }

    /**
     * Get the {@link edu.wpi.first.wpilibj.DriverStation} instance
     */
    public DriverStation station() {
        return m_ds;
    }

    /**
     * Get the default logger for the Toast API. This logger uses
     * ATTR_DEFAULT, including Date, Time and current Thread.
     */
    public static Logger log() {
        return ToastBootstrap.toastLogger;
    }

    /**
     * Yum
     */
    public static String getRandomTaste() {
        return tastes[new Random().nextInt(tastes.length)];
    }

    /**
     * Executed when WPILib detects this class and loads it. This
     * is used for Pre-Initialization Tasks.
     */
    @Override
    protected void prestart() {
        try {
            // -------- NEW PHASE -------- //
            LoadPhase.PRE_START.transition();
            log().info("Buttering Bread...");
            GroovyLoader.init();
            RobotLoader.init();

            CommandBus.init();

            USBMassStorage.load();
            RobotLoader.prestart();
            GroovyLoader.prestart();
            FRCHooks.robotReady();
        } catch (Exception e) {
            CrashHandler.handle(e);
        }
    }

    /**
     * The robot is setup and ready to go. Let's rock.
     */
    @Override
    public void startCompetition() {
        try {
            // -------- NEW PHASE -------- //
            LoadPhase.START.transition();
            log().info("Fabricating Sandwich...");

            log().info("Verdict: " + getRandomTaste());
            RobotLoader.start();
            GroovyLoader.start();

            SocketManager.launch();

            if (ToastConfiguration.Property.OPTIMIZATION_GC.asBoolean()) {
                registerGC(ToastConfiguration.Property.OPTIMIZATION_GC_TIME.asDouble());
            }

            LoadPhase.COMPLETE.transition();
            ToastConfiguration.jetfuel();
            ToastBootstrap.endTimeMS = System.currentTimeMillis();
            log().info("Total Initiation Time: " + (double)(ToastBootstrap.endTimeMS - ToastBootstrap.startTimeMS) / 1000D + " seconds");
            StateTracker.init(this);
        } catch (Exception e) {
            CrashHandler.handle(e);
        }
    }

    /**
     * Register the Garbage Collector to trigger on a regular basis.
     */
    void registerGC(final double time) {
        Heartbeat.add(new HeartbeatListener() {
            int count = 0;
            @Override
            public void onHeartbeat(int skipped) {
                count++;
                if (count >= 10 * time) {
                    System.gc();
                    count = 0;
                }
            }
        });
    }

    /**
     * Shutdown the robot safely with exit code 0. This will stop all motors and
     * ensure all actions on the ThreadPool are completed
     */
    public void shutdownSafely() {
        log().info("Robot Shutting Down...");
        ToastThreadPool.INSTANCE.finish();
        MotorRegistry.stopAll();
        System.exit(0);
    }

    /**
     * Shutdown the robot when the exit code should not be 0. This is usually when
     * the robot has crashed or encountered and error. This will forcefully stop the ThreadPool,
     * all motors and yield the exit code -1
     */
    public void shutdownCrash() {
        log().info("Robot Error Detected... Shutting Down...");
        ToastThreadPool.INSTANCE.getService().shutdownNow();
        MotorRegistry.stopAll();
        System.exit(-1);
    }

}
