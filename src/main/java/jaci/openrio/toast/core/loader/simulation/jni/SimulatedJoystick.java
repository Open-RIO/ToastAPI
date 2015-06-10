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

    public SimulatedJoystick(int buttonCount, int povCount) {
        this.buttonCount = buttonCount;
        this.buttons = new boolean[buttonCount];
        axisV = new double[5];
        povs = new int[povCount];
        for (int i = 0; i < povCount; i++)
            povs[i] = -1;
    }

    public void setButton(int button, boolean state) {
        buttons[button] = state;
    }

    public boolean[] getButtons() {
        return buttons;
    }

    public void setPOV(int id, int value) {
        povs[id] = value;
    }

    public int[] getPOVs() {
        return povs;
    }

    public int getPOV(int id) {
        return povs[id];
    }

    /**
     * 0: X
     * 1: Y
     * 2: Z
     * 3: Twist
     * 4: Throttle
     */
    public void setAxis(int axis, double value) {
        axisV[axis] = value;
    }

    public double[] getAxis() {
        return axisV;
    }

    public double getAxis(int axis) {
        return axisV[axis];
    }

    public short[] encodeAxis() {          //encodes to Short values to be read by WPILib
        short[] vals = new short[axisV.length];
        for (int i = 0; i < vals.length; i++) {
            vals[i] = (short) (axisV[i] * 127);
        }
        return vals;
    }

    public short[] encodePOV() {
        short[] vals = new short[povs.length];
        for (int i = 0; i < vals.length; i++)
            vals[i] = (short) povs[i];
        return vals;
    }

    public int encodeButtons() {           //encodes to 0b000000000000 Where each digit can be an on/off state
        int base = 0x0;
        for (int i = buttonCount - 1; i >= 0; i--) {
            base = base << 1;
            base = base | (buttons[i] ? 0b1 : 0b0);
        }
        return base;
    }

}
