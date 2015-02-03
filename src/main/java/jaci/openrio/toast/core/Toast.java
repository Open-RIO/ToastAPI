package jaci.openrio.toast.core;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import jaci.openrio.toast.core.loader.RobotLoader;
import jaci.openrio.toast.core.monitoring.power.PDPMonitor;
import jaci.openrio.toast.core.webui.WebRegistry;
import jaci.openrio.toast.core.webui.handlers.HandlerConsole;
import jaci.openrio.toast.core.webui.handlers.HandlerPower;
import jaci.openrio.toast.lib.FRCHooks;
import jaci.openrio.toast.lib.crash.CrashHandler;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.log.SysLogProxy;

import java.util.Random;

/**
 * The Toast Base Class. This is the base for the Toast API.
 * In order to take advantage of this, change your Robot-Class
 * to this instead of your own in the Manifest.MF file.
 *
 * @author Jaci
 */
public class Toast extends RobotBase {

    private static Logger log;
    private static String[] tastes = new String[] {"Delicious", "Yummy", "Like a buttery heaven", "Needs more salt", "Hot, Hot, HOT!!"};

    private static Toast instance;

    public Toast() {
        super();
        instance = this;
    }

    public static Toast getToast() {
        return instance;
    }

    public DriverStation station() {
        return m_ds;
    }

    /**
     * Get the default logger for the Toast API. This logger uses
     * ATTR_DEFAULT, including Date, Time and current Thread.
     */
    public static Logger log() {
        if (log == null) log = new Logger("Toast", Logger.ATTR_DEFAULT);
        return log;
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
        Thread.currentThread().setName("Pre-Initialization");
        SysLogProxy.init();
        log().info("Slicing Loaf...");
        CrashHandler.init();
        RobotLoader.init();
        WebRegistry.init();

        log().info("Nuking Toast...");
        RobotLoader.getRobot().prestart();
        FRCHooks.robotReady();
    }

    /**
     * The robot is setup and ready to go. Let's rock.
     */
    @Override
    public void startCompetition() {
        Thread.currentThread().setName("Initialization");
        log().info("Buttering Bread...");
        PDPMonitor.init();
        WebRegistry.addHandler(new HandlerPower());
        WebRegistry.addHandler(new HandlerConsole());

        log().info("Fabricating Sandwich...");
        Thread.currentThread().setName("Main");
        log().info("Verdict: " + getRandomTaste());
        RobotLoader.getRobot().startCompetition();
        StateTracker.init(this);
    }

}
