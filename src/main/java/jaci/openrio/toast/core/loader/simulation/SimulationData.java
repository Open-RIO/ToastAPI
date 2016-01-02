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
        if (SimulationGUI.INSTANCE != null)
            SimulationGUI.INSTANCE.dioSpinners[port].setValue(val);
    }

    /**
     * Set the digital IO direction on the given port, as Input or Output
     */
    public static void setDIODir(byte port, byte val) {
        dioDirections[port] = val;
        if (SimulationGUI.INSTANCE != null)
            SimulationGUI.INSTANCE.dioSpinners[port].setEditable(val == 1);
    }

    /**
     * DIGITAL GLITCH FILTER *
     */
    public static int[] glitchFilters = new int[10];
    public static int[] filterDurations = new int[20];

    /**
     * PWM OUT *
     */
    public static double[] pwmValues = new double[10];

    /**
     * Set the PWM value on the given port with the given value
     */
    public static void setPWM(byte port, double val) {
        pwmValues[port] = val;
        if (SimulationGUI.INSTANCE != null)
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

    /** RELAYS **/
    public static boolean[] relay_fwd = new boolean[4];
    public static boolean[] relay_rvs = new boolean[4];

    /**
     * Set a Relay to a given Forward/Reverse mode
     */
    public static void setRelay(byte port, boolean forward, boolean on) {
        if (forward)
            relay_fwd[port] = on;
        else
            relay_rvs[port] = on;

        if (SimulationGUI.INSTANCE != null) {
            SimulationGUI.INSTANCE.relays[port].setForward(relay_fwd[port]);
            SimulationGUI.INSTANCE.relays[port].setReverse(relay_rvs[port]);
        }
    }

    /** PNEUMATICS **/
    public static boolean[] enabled_compressors = new boolean[3];
    public static boolean[] loop_compressors = new boolean[3];
    public static boolean[] compressor_pressure = new boolean[3];
    public static float[] compressor_current = new float[3];
    public static boolean[][] solenoids = new boolean[3][8];

    /**
     * Set a compressor with the Module ID to be connected
     */
    public static void setCompressor(byte id) {
        enabled_compressors[id] = true;
        if (PneumaticsGUI.INSTANCE != null)
            PneumaticsGUI.INSTANCE.pcms[id].repaint();
    }

    /**
     * Set the compressor in the current ID to be in a run loop (running w/ pressure switch)
     */
    public static void setCompressorLoop(byte id, boolean loop) {
        loop_compressors[id] = loop;
        if (PneumaticsGUI.INSTANCE != null)
            PneumaticsGUI.INSTANCE.pcms[id].repaint();
    }

    /**
     * Set the compressor pressure switch to on or off
     */
    public static void setCompressorPressureSwitch(byte id, boolean state) {
        compressor_pressure[id] = state;
        if (PneumaticsGUI.INSTANCE != null)
            PneumaticsGUI.INSTANCE.pcms[id].repaint();
    }

    /**
     * Check if the compressor is running
     */
    public static boolean compressorRunning(byte id) {
        return enabled_compressors[id] && loop_compressors[id] && !compressor_pressure[id];
    }

    public static void setSolenoid(byte module, byte channel, boolean state) {
        solenoids[module][channel] = state;
        if (PneumaticsGUI.INSTANCE != null)
            PneumaticsGUI.INSTANCE.pcms[module].repaint();
    }

    /** JOYSTICKS **/

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
    public static int alliance_station = 0;

    /**
     * Repaint the State of the Simulation GUI
     */
    public static void repaintState() {
        if (SimulationGUI.INSTANCE != null)
            SimulationGUI.INSTANCE.repaintState();
    }

}
