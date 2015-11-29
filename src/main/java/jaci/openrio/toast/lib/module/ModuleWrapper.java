package jaci.openrio.toast.lib.module;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.SampleRobot;
import jaci.openrio.toast.core.StateTracker;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.loader.annotation.NoLoad;
import jaci.openrio.toast.core.loader.module.ModuleCandidate;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.io.File;
import java.lang.reflect.*;
import java.util.HashMap;

/**
 * The 'ModuleWrapper' is a Wrapper for the ToastModule class around classes implementing RobotBase. The Goal of this
 * class is to support loading of compiled FRC Program Jars that aren't designed for Toast (e.g. FRCUserProgram.jar).
 * This allows importing of code that isn't designed for module use.
 *
 * Keep in mind that only RobotBase and IterativeRobot are supported, with SampleRobot being half-supported (no ticking
 * support). StartCompetition is started in a new thread for code that substitutes its own periodic ticker, although
 * this usage is discouraged.
 *
 * @author Jaci
 */
@NoLoad
public class ModuleWrapper extends ToastModule {

    File parentFile;
    RobotBase base;
    Class<? extends RobotBase> baseClass;
    ModuleCandidate candidate;
    boolean isIterative;
    boolean isSample;

    public static Unsafe theUnsafe;

    public ModuleWrapper(File parentFile, Class<? extends RobotBase> robotBaseClass, ModuleCandidate candidate) {
        this.parentFile = parentFile;
        this.baseClass = robotBaseClass;
        this.candidate = candidate;
    }

    @Override
    public void onConstruct() {
        try {
            // Thanks, NetworkTables
            if (theUnsafe == null) {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                theUnsafe = (Unsafe) f.get(null);
            }
            this.base = baseClass.cast(theUnsafe.allocateInstance(baseClass));
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            Toast.log().error("Could not instantiate Wrapped Module: " + e);
        }

        this.isIterative = base instanceof IterativeRobot;
        this.isSample = base instanceof SampleRobot;
    }

    @Override
    public String getModuleName() {
        return parentFile.getName() + "!wrapper";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.0";
    }

    @Override
    public void prestart() {
        try {
            Field m_ds = RobotBase.class.getDeclaredField("m_ds");
            m_ds.setAccessible(true);
            m_ds.set(base, DriverStation.getInstance());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Toast.log().error("Error while setting DriverStation for wrapped module: " + e);
        }

        reflectMethod("prestart", base);
    }

    @Override
    public void start() {
        Thread thread = new Thread(() -> {
            if (isSample || isIterative)
                reflectMethod("robotInit", base);
            else {
                base.startCompetition();
            }
            stateSetup();
        });
        thread.start();
    }

    public HashMap<String, String> getCustomData() {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("Iterative", Boolean.toString(isIterative));
        hash.put("Sample", Boolean.toString(isSample));
        hash.put("Wrapped Class", base.getClass().getName());
        return hash;
    }

    public void stateSetup() {
        if (isIterative) {
            IterativeRobot robot = (IterativeRobot) base;
            StateTracker.addTicker((state) -> {
                switch (state) {
                    case DISABLED:
                        robot.disabledPeriodic(); break;
                    case AUTONOMOUS:
                        robot.autonomousPeriodic(); break;
                    case TELEOP:
                        robot.teleopPeriodic(); break;
                    case TEST:
                        robot.testPeriodic(); break;
                }
            });

            StateTracker.addTransition((state, oldstate) -> {
                switch (state) {
                    case DISABLED:
                        robot.disabledInit(); break;
                    case AUTONOMOUS:
                        robot.autonomousInit(); break;
                    case TELEOP:
                        robot.teleopInit(); break;
                    case TEST:
                        robot.testInit(); break;
                }
            });
        }
    }

    public void reflectMethod(String method, Object base) {
        // Why WPILib decided to leave these methods as 'protected' I will never know
        // Kids, don't do this.

        Method m = getMethod(method, base);
        if (m == null)
            Toast.log().error("Could not instantiate Wrapped Module reflection on " + method + " as it does not exist!");
        else {
            try {
                m.setAccessible(true);
                m.invoke(base);
            } catch (InvocationTargetException | IllegalAccessException e) {
                Toast.log().error("Could not instantiate Wrapped Module reflection on " + method +  ": " + e);
                Toast.log().exception(e);
            }
        }
    }

    public Method getMethod(String theMethod, Object base) {
        Class<?> clazz = base.getClass();
        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                // Test any other things about it beyond the name...
                if (method.getName().equals(theMethod)) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

}
