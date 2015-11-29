package jaci.openrio.toast.lib.device;

import edu.wpi.first.wpilibj.Joystick;

import java.util.Arrays;

/**
 * Wrapper class for an Xbox 360 Controller. This provides all the necessary mappings for a standard Xbox 360 Controller.
 *
 * @author Jaci
 */
public class XboxController {

    private Joystick _joystick;

    public XboxController(int port) {
        _joystick = new Joystick(port);
    }

    /**
     * Get the underlying Joystick instance
     */
    public Joystick getUnderlyingJoystick() {
        return _joystick;
    }

    /**
     * @return The value of the left trigger, ranging from 0 to 1
     */
    public double leftTrigger() {
        return _joystick.getRawAxis(2);
    }

    /**
     * @return The value of the right trigger, ranging from 0 to 1
     */
    public double rightTrigger() {
        return _joystick.getRawAxis(3);
    }

    /**
     * @return The value of the Left Thumbstick's X axis, ranging from -1 to 1
     */
    public double leftX() {
        return _joystick.getRawAxis(0);
    }

    /**
     * @return The value of the Left Thumbstick's Y axis, ranging from -1 to 1
     */
    public double leftY() {
        return _joystick.getRawAxis(1);
    }

    /**
     * @return The value of the Right Thumbstick's X axis, ranging from -1 to 1
     */
    public double rightX() {
        return _joystick.getRawAxis(4);
    }

    /**
     * @return The value of the Right Thumbstick's Y axis, ranging from -1 to 1
     */
    public double rightY() {
        return _joystick.getRawAxis(5);
    }

    /**
     * @return The heading of the POV (D-Pad) on the controller, in degrees.
     */
    public int pov() {
        return _joystick.getPOV();
    }

    /**
     * @return The direction of the POV (D-Pad) on the controller
     */
    public POV povDirection() {
        return Arrays.stream(POV.values()).filter(pred -> pred.direction == pov()).findFirst().get();
    }

    /**
     * @return A Button press state
     */
    public boolean a() {
        return _joystick.getRawButton(1);
    }

    /**
     * @return B Button press state
     */
    public boolean b() {
        return _joystick.getRawButton(2);
    }

    /**
     * @return X Button press state
     */
    public boolean x() {
        return _joystick.getRawButton(3);
    }

    /**
     * @return Y Button press state
     */
    public boolean y() {
        return _joystick.getRawButton(4);
    }

    /**
     * @return Left Bumper press state
     */
    public boolean leftBumper() {
        return _joystick.getRawButton(5);
    }

    /**
     * @return Right Button press state
     */
    public boolean rightBumper() {
        return _joystick.getRawButton(6);
    }

    /**
     * @return Select (back) Button press state
     */
    public boolean select() {
        return _joystick.getRawButton(7);
    }

    /**
     * @return Start Button press state
     */
    public boolean start() {
        return _joystick.getRawButton(8);
    }

    /**
     * @return Left Thumbstick Button press state
     */
    public boolean leftStick() {
        return _joystick.getRawButton(9);
    }

    /**
     * @return Right Thumbstick Button press state
     */
    public boolean rightStick() {
        return _joystick.getRawButton(10);
    }

    /**
     * @return The absolute magnitude of the Left Thumbstick from the Origin, ranging from 0 to 1
     */
    public double leftStickMagnitude() {
        return Math.sqrt(leftX()*leftX() + leftY()*leftY());
    }

    /**
     * @return The absolute magnitude of the Right Thumbstick from the Origin, ranging from 0 to 1
     */
    public double rightStickMagnitude() {
        return Math.sqrt(rightX()*rightX() + rightY()*rightY());
    }

    /**
     * @return The heading (direction) of the Left Thumbstick, ranging from -180 to 180 degrees
     */
    public double leftStickHeading() {
        return Math.atan2(leftX(), -leftY()) * 180 / Math.PI;
    }

    /**
     * @return The heading (direction) of the Right Thumbstick, ranging from -180 to 180 degrees
     */
    public double rightStickHeading() {
        return Math.atan2(rightX(), -rightY()) * 180 / Math.PI;
    }
    
}
