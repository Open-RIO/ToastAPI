package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class SolenoidJNI extends JNIWrapper {

    public static long initializeSolenoidPort(long portPointer) {
        return portPointer;
    }

    public static void freeSolenoidPort(long port_pointer) {}

    public static long getPortWithModule(byte module, byte channel) {
        long bits = 0L;                 // Internal bit shifting to save an Association Array
        bits = bits | module;
        bits = bits << 8;
        bits = bits | channel;
        return bits;
    }

    public static void setSolenoid(long bits, boolean on) {
        byte module = (byte) (bits >> 8 & 0xff);
        byte channel = (byte) (bits & 0xff);
        SimulationData.setSolenoid(module, channel, on);
    }

    public static boolean getSolenoid(long bits) {
        byte module = (byte) (bits >> 8 & 0xff);
        byte channel = (byte) (bits & 0xff);
        return SimulationData.solenoids[module][channel];
    }

    public static byte getAllSolenoids(long port) {
        byte module = (byte) (port >> 8 & 0xff);
        byte data = 0;
        for (int i = 0; i < 8; i++)
            if (SimulationData.solenoids[module][i]) data |= 1 << i;
        return data;
    }

    public static int getPCMSolenoidBlackList(long pcm_pointer) { return 0; };
    public static boolean getPCMSolenoidVoltageStickyFault(long pcm_pointer) { return false; }
    public static boolean getPCMSolenoidVoltageFault(long pcm_pointer) { return false; }
    public static void clearAllPCMStickyFaults(long pcm_pointer) {}
}
