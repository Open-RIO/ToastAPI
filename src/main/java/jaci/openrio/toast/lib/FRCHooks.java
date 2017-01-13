package jaci.openrio.toast.lib;

import edu.wpi.first.wpilibj.hal.HAL;

/**
 * Common hooks to the FRC methods with ridiculously long method names. WPI pls.
 *
 * @author Jaci
 */
public class FRCHooks {

    /**
     * Called when the robot is ready to be enabled (pre-init phase complete)
     */
    public static void robotReady() {
        HAL.observeUserProgramStarting();
    }

    /**
     * Observe the input from the driver station for the 'disabled' mode
     */
    public static void observeDisabled() {
        HAL.observeUserProgramDisabled();
    }

    /**
     * Observe the input from the driver station for the 'test' mode
     */
    public static void observeTest() {
        HAL.observeUserProgramTest();
    }

    /**
     * Observe the input from the driver station for the 'teleop' mode
     */
    public static void observeTeleop() {
        HAL.observeUserProgramTeleop();
    }

    /**
     * Observe the input from the driver station for the 'autonomous' mode
     */
    public static void observeAutonomous() {
        HAL.observeUserProgramAutonomous();
    }

}
