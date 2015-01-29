package jaci.openrio.toast.lib;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;

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
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramStarting();
    }

    /**
     * Observe the input from the driver station for the 'disabled' mode
     */
    public static void observeDisabled() {
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramDisabled();
    }

    /**
     * Observe the input from the driver station for the 'test' mode
     */
    public static void observeTest() {
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTest();
    }

    /**
     * Observe the input from the driver station for the 'teleop' mode
     */
    public static void observeTeleop() {
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTeleop();
    }

    /**
     * Observe the input from the driver station for the 'autonomous' mode
     */
    public static void observeAutonomous() {
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramAutonomous();
    }

}
