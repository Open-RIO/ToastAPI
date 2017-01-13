package jaci.openrio.toast.core.loader.simulation.jni;

/**
 * An instance of a Simulated Joystick Controller. This is used to simulate joystick control in a simulation environment,
 * as the default FRC driver station does not work in Simulated Environments.
 *
 * @author Jaci
 */
public class SimulatedJoystick {

    int buttonCount;
    boolean[] buttons;
    double[] axisV;
    int[] povs;

    public SimulatedJoystick(int buttonCount, int axisCount, int povCount) {
        this.buttonCount = buttonCount;
        this.buttons = new boolean[buttonCount];
        axisV = new double[axisCount];
        povs = new int[povCount];
        for (int i = 0; i < povCount; i++)
            povs[i] = -1;
    }

    /**
     * Set the given button id (0-indexed) to true or false
     */
    public void setButton(int button, boolean state) {
        buttons[button] = state;
    }

    /**
     * Get the Simulated Button states set with the {@link #setButton(int, boolean)} method
     */
    public boolean[] getButtons() {
        return buttons;
    }

    /**
     * Set the POV value on the given ID. This usually consists of 8 settings (0..7) indexed from "Up"
     */
    public void setPOV(int id, int value) {
        povs[id] = value;
    }

    /**
     * Get the Simulated POV values set with the {@link #setPOV(int, int)} method
     */
    public int[] getPOVs() {
        return povs;
    }

    /**
     * Get the specified POV value set with {@link #setPOV(int, int)}
     */
    public int getPOV(int id) {
        return povs[id];
    }

    /**
     * Set the given Axis ID with the specified value (usually from -1..1). This changes per Joystick type
     */
    public void setAxis(int axis, double value) {
        axisV[axis] = value;
    }

    /**
     * Get the collective values of every axis. This is from the values set with {@link #setAxis(int, double)}
     */
    public double[] getAxis() {
        return axisV;
    }

    /**
     * Get a specified axis defined in {@link #setAxis(int, double)}
     */
    public double getAxis(int axis) {
        return axisV[axis];
    }

    /**
     * Encode all the Axis to a short system that can be read by WPILib. This is to a resolution of 128 in either
     * direction.
     */
    public short[] encodeAxis() {          //encodes to Short values to be read by WPILib
        short[] vals = new short[axisV.length];
        for (int i = 0; i < vals.length; i++) {
            vals[i] = (short) (axisV[i] * 127);
        }
        return vals;
    }

    /**
     * Encode the POV to a short system that WPILib can read and parse into it's Joystick class.
     */
    public short[] encodePOV() {
        short[] vals = new short[povs.length];
        for (int i = 0; i < vals.length; i++)
            vals[i] = (short) povs[i];
        return vals;
    }

    /**
     * Encode the buttons into a single integer, where each bit of the 32 bit sequence defines a button state.
     */
    public int encodeButtons() {           //encodes to 0b000000000000 Where each digit can be an on/off state
        int base = 0x0;
        for (int i = buttonCount - 1; i >= 0; i--) {
            base = base << 1;
            base = base | (buttons[i] ? 0b1 : 0b0);
        }
        return base;
    }

}
