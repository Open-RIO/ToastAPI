package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.lib.state.RobotState;

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

    public static void setDIO(byte port, byte val) {
        dioValues[port] = val;
        SimulationGUI.INSTANCE.dioSpinners[port].setValue(val);
    }

    public static void setDIODir(byte port, byte val) {
        dioDirections[port] = val;
        SimulationGUI.INSTANCE.dioSpinners[port].setEditable(val == 1);
    }

    /**
     * PWM OUT *
     */
    public static double[] pwmValues = new double[10];

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

    public static RobotState currentState = RobotState.DISABLED;

}
