package jaci.openrio.toast.core.loader.verification;

import groovy.lang.GroovyObject;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.loader.groovy.GroovyLoader;
import jaci.openrio.toast.core.loader.module.ModuleContainer;
import jaci.openrio.toast.core.loader.module.ModuleManager;
import jaci.openrio.toast.core.loader.simulation.SimulationData;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.GroovyScript;
import jaci.openrio.toast.lib.state.RobotState;

import java.util.Map;

/**
 * The Verification Worker. This class is called when the Toast Jar file is launched with the arguments -vf or -verify.
 * This thread runs through the Disabled -> Autonomous -> Teleoperated -> End lifecycle of the robot, and if any errors
 * or uncaught exceptions are encountered along the way, the program will exit with error code -1. If this is launched from
 * gradle using 'gradlew verify', the build will fail. This class is mostly used in Travis CI builds and exists to give
 * a proper 'yes, this code works' requirement to a successful build instead of the typical 'there's no compile errors'
 * requirement, giving repos an accurate look at how the build is passing.
 *
 * @author Jaci
 */
public class VerificationWorker extends Thread {

    static VerificationWorker worker;
    static Logger logger;

    public VerificationWorker() {
        this.setName("Verification Worker");
    }

    /**
     * Begin the Verification Routine. This is called automatically by Toast when launched with the --verify arguments
     */
    public static void begin() {
        logger = new Logger("Verification", Logger.ATTR_TIME);
        worker = new VerificationWorker();
        worker.start();
    }

    /**
     * Run a single loop of the Verification Routine.
     */
    @Override
    public void run() {
        try {
            logger.info("Beginning Code Verification. Echoing environmental data...");
            echoEnvironment();
            logger.info("Starting Code Verification in 5 seconds...");
            Thread.sleep(5 * 1000);
            logger.info("Transferring into Autonomous... (Switching to Teleop in 15 seconds)");
            SimulationData.currentState = RobotState.AUTONOMOUS;
            Thread.sleep(15 * 1000);
            logger.info("Transferring into Teleoperated... (Switching to Disabled in 1 minute)");
            SimulationData.currentState = RobotState.TELEOP;
            Thread.sleep(60 * 1000);
            logger.info("Disabling Robot... (Shutting Down in 10 seconds)");
            SimulationData.currentState = RobotState.DISABLED;
            Thread.sleep(10 * 1000);
            logger.info("Code Verification Successful! Shutting Down...");
            Toast.getToast().shutdownSafely();
        } catch (Exception e) {
            logger.error("Exception encountered during verification: " + e);
            logger.exception(e);
            Toast.getToast().shutdownCrash();
        }
    }

    /**
     * Print relevant environmental data to the console.
     */
    void echoEnvironment() {
        logger.info("\tLoaded Modules:");
        for (ModuleContainer module : ModuleManager.getContainers())
            logger.info("\t\t" + module.getDetails());

        logger.info("\tLoaded Groovy Scripts:");
        for (GroovyScript script : GroovyLoader.scripts)
            logger.info("\t\t" + script.getClass());

        logger.info("\tLoaded Groovy Files:");
        for (Map.Entry<String, GroovyObject> entry : GroovyLoader.groovyObjects.entrySet())
            logger.info("\t\t" + entry.getKey() + " : " + entry.getValue());

        
    }

}
