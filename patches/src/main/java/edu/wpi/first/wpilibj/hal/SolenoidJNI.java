package edu.wpi.first.wpilibj.hal;

import com.sun.tools.javadoc.Start;
import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class SolenoidJNI extends JNIWrapper {

    public static int initializeSolenoidPort(int halPortHandle) {
        return halPortHandle;
    }

    public static boolean checkSolenoidModule(int module) {
        return true;
    }

    public static boolean checkSolenoidChannel(int channel) {
        return true;
    }

    public static void freeSolenoidPort(int portHandle) { }

    public static void setSolenoid(int portHandle, boolean on) {
        byte[] pm = getPortAndModuleFromHandle(portHandle);
        SimulationData.setSolenoid(pm[0], pm[1], on);
    }

    public static boolean getSolenoid(int portHandle) {
        byte[] pm = getPortAndModuleFromHandle(portHandle);
        return SimulationData.solenoids[pm[0]][pm[1]];
    }

    public static byte getAllSolenoids(byte module) {
        byte data = 0;
        for (int i = 0; i < 8; i++)
            if (SimulationData.solenoids[module][i]) data |= 1 << i;
        return data;
    }

    public static int getPCMSolenoidBlackList(byte module) {
        return 0;
    }

    public static boolean getPCMSolenoidVoltageStickyFault(byte module) {
        return false;
    }

    public static boolean getPCMSolenoidVoltageFault(byte module) {
        return false;
    }

    public static void clearAllPCMStickyFaults(byte module) { }
}
