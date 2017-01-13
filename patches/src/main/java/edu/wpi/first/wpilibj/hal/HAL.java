package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.DriverStationCommunications;
import jaci.openrio.toast.core.loader.simulation.SimulationData;
import jaci.openrio.toast.core.loader.simulation.jni.SimulatedJoystick;
import jaci.openrio.toast.lib.state.RobotState;

import java.nio.ByteBuffer;

@SuppressWarnings({"AbbreviationAsWordInName", "MethodName"})
public class HAL extends JNIWrapper {
    public static void waitForDSData() {
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) { }
    }

    public static int initialize(int mode) { return 0; }

    public static void observeUserProgramStarting() { }

    public static void observeUserProgramDisabled() { }

    public static void observeUserProgramAutonomous() { }

    public static void observeUserProgramTeleop() { }

    public static void observeUserProgramTest() { }

    public static void report(int resource, int instanceNumber) {
        report(resource, instanceNumber, 0, "");
    }

    public static void report(int resource, int instanceNumber, int context) {
        report(resource, instanceNumber, context, "");
    }

    /**
     * Report the usage of a resource of interest. <br>
     *
     * <p>Original signature: <code>uint32_t report(tResourceType, uint8_t, uint8_t, const
     * char*)</code>
     *
     * @param resource       one of the values in the tResourceType above (max value 51). <br>
     * @param instanceNumber an index that identifies the resource instance. <br>
     * @param context        an optional additional context number for some cases (such as module
     *                       number). Set to 0 to omit. <br>
     * @param feature        a string to be included describing features in use on a specific
     *                       resource. Setting the same resource more than once allows you to change
     *                       the feature string.
     */
    public static int report(int resource, int instanceNumber, int context, String feature) {
        return 0;
    }

    private static int nativeGetControlWord() {
        return 0;   // There is a TODO here, base this off robot state
    }

    @SuppressWarnings("JavadocMethod")
    public static void getControlWord(ControlWord controlWord) {
        RobotState s = SimulationData.currentState;
        controlWord.update(s != RobotState.DISABLED, s == RobotState.AUTONOMOUS, s == RobotState.TEST,
                false, false, true);
    }

    private static int nativeGetAllianceStation() {
        return SimulationData.alliance_station;
    }

    @SuppressWarnings("JavadocMethod")
    public static AllianceStationID getAllianceStation() {
        switch (nativeGetAllianceStation()) {
            case 0:
                return AllianceStationID.Red1;
            case 1:
                return AllianceStationID.Red2;
            case 2:
                return AllianceStationID.Red3;
            case 3:
                return AllianceStationID.Blue1;
            case 4:
                return AllianceStationID.Blue2;
            case 5:
                return AllianceStationID.Blue3;
            default:
                return null;
        }
    }

    public static int kMaxJoystickAxes = 12;
    public static int kMaxJoystickPOVs = 12;

    public static short getJoystickAxes(byte joystickNum, float[] axesArray) {
        if (DriverStationCommunications.connected) {
            short count = DriverStationCommunications.joyaxiscount[joystickNum];
            for (int i = 0; i < count; i++)
                axesArray[i] = (float)DriverStationCommunications.joyaxis[joystickNum][i] / 127.0f;
            return count;
        }
        SimulatedJoystick simJoy = SimulationData.getJoystick(joystickNum);
        for (int i = 0; i < simJoy.getAxis().length; i++)
            axesArray[i] = (float)simJoy.getAxis(i);
        return (short)simJoy.getAxis().length;
    }

    public static short getJoystickPOVs(byte joystickNum, short[] povsArray) {
        if (DriverStationCommunications.connected) {
            short count = DriverStationCommunications.joypovcount[joystickNum];
            for (int i = 0; i < count; i++)
                povsArray[i] = DriverStationCommunications.joypov[joystickNum][i];
            return count;
        }
        SimulatedJoystick simJoy = SimulationData.getJoystick(joystickNum);
        for (int i = 0; i < simJoy.getPOVs().length; i++)
            povsArray[i] = (short)simJoy.getPOV(i);
        return (short)simJoy.getPOVs().length;
    }

    public static int getJoystickButtons(byte joystickNum, ByteBuffer count) {
        if (DriverStationCommunications.connected) {
            count.put(0, DriverStationCommunications.joybuttoncount[joystickNum]);
            return DriverStationCommunications.joybuttons[joystickNum];
        }
        SimulatedJoystick joystick = SimulationData.getJoystick(joystickNum);
        count.put(0, (byte) joystick.getButtons().length);
        return joystick.encodeButtons();
    }

    public static int setJoystickOutputs(byte joystickNum, int outputs, short leftRumble, short rightRumble) {
        return 0;
    }

    public static int getJoystickIsXbox(byte joystickNum) {
        return 0;
    }

    public static int getJoystickType(byte joystickNum) {
        return 0;
    }

    public static String getJoystickName(byte joystickNum) {
        return "Generic";
    }

    public static int getJoystickAxisType(byte joystickNum, byte axis) {
        return 0;
    }

    public static double getMatchTime() {
        return 120;
    }

    public static boolean getSystemActive() {
        return true;
    }

    public static boolean getBrownedOut() {
        return false;
    }

    public static int setErrorData(String error) {
        return 0;
    }

    public static int sendError(boolean isError, int errorCode, boolean isLVCode,
                                       String details, String location, String callStack,
                                       boolean printMsg) {
        return 0;
    }
}
