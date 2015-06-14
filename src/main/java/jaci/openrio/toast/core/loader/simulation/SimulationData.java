package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.core.loader.simulation.jni.DummyJoystick;
import jaci.openrio.toast.core.loader.simulation.jni.SimulatedJoystick;
import jaci.openrio.toast.lib.state.RobotState;

/**
 * This class stores and manipulates the data that is used for Robot simulation. Patch classes should hook into this
 * to store their data
 *
 * @author Jaci
 */
public class SimulationData {

    /**
     * ACCELEROMETER *
     */
    public static double[] accelerometer = {0D, 0D, 0D};
    public static double accelerometerRange = 0D;
    public static boolean accelerometerEnabled = false;

    /**
     * DIGITAL IO *
     */
    public static byte[] dioValues = new byte[10];
    public static byte[] dioDirections = new byte[10];

    /**
     * Set the Digital IO on the given port with the given value
     */
    public static void setDIO(byte port, byte val) {
        dioValues[port] = val;
        SimulationGUI.INSTANCE.dioSpinners[port].setValue(val);
    }

    /**
     * Set the digital IO direction on the given port, as Input or Output
     */
    public static void setDIODir(byte port, byte val) {
        dioDirections[port] = val;
        SimulationGUI.INSTANCE.dioSpinners[port].setEditable(val == 1);
    }

    /**
     * PWM OUT *
     */
    public static double[] pwmValues = new double[10];

    /**
     * Set the PWM value on the given port with the given value
     */
    public static void setPWM(byte port, double val) {
        pwmValues[port] = val;
        SimulationGUI.INSTANCE.pwmSpinners[port].setValue(val);
    }

    /**
     * POWER DISTRIBUTION PANEL *
     */
    public static double pdpTemperature = 30D;
    public static double pdpVoltage = 12.5D;
    public static double[] pdpChannelCurrent = new double[16];

    /**
     * ROBORIO POWER *
     */
    public static double powerVinVoltage = 12.5F;
    public static double powerVinCurrent = 0F;

    public static double powerUserVoltage6V = 0F;
    public static double powerUserCurrent6V = 0F;
    public static boolean powerUserActive6V = true;
    public static int powerUserFaults6V = 0;

    public static double powerUserVoltage5V = 0F;
    public static double powerUserCurrent5V = 0F;
    public static boolean powerUserActive5V = true;
    public static int powerUserFaults5V = 0;

    public static double powerUserVoltage3V3 = 0F;
    public static double powerUserCurrent3V3 = 0F;
    public static boolean powerUserActive3V3 = true;
    public static int powerUserFaults3V3 = 0;

    /** BUTTONS **/
    public static boolean userButtonPressed = false;

    /** JOYSTICKS **/
    public static SimulatedJoystick[] joysticks = new SimulatedJoystick[6];

    /**
     * Register a Simulated Joystick on the Data class
     */
    public static void simulatedJoystick(int id, SimulatedJoystick stick) {
        joysticks[id] = stick;
    }

    /**
     * Get a Simulated Joystick interface for the joystick ID, or create a dummy if it doesn't exist.
     */
    public static SimulatedJoystick getJoystick(int id) {
        if (joysticks[id] == null)
            joysticks[id] = new DummyJoystick();
        return joysticks[id];
    }

    public static RobotState currentState = RobotState.DISABLED;

}
