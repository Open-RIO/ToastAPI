package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

import java.nio.IntBuffer;
import java.nio.ByteBuffer;

public class SolenoidJNI extends JNIWrapper {

    public static ByteBuffer initializeSolenoidPort(ByteBuffer portPointer, IntBuffer status) {
        return portPointer;
    }

    public static ByteBuffer getPortWithModule(byte module, byte channel) {
        return ByteBuffer.allocate(2).put(new byte[]{module, channel});
    }

    public static void setSolenoid(ByteBuffer port, byte on, IntBuffer status) {
        byte module = port.get(0);
        byte channel = port.get(1);
        SimulationData.setSolenoid(module, channel, on != 0);
    }

    public static byte getSolenoid(ByteBuffer port, IntBuffer status) {
        byte module = port.get(0);
        byte channel = port.get(1);
        return (byte) (SimulationData.solenoids[module][channel] ? 1 : 0);
    }

    public static native byte getPCMSolenoidBlackList(ByteBuffer pcm_pointer, IntBuffer status);
    public static boolean getPCMSolenoidVoltageStickyFault(ByteBuffer pcm_pointer, IntBuffer status) { return false; }
    public static boolean getPCMSolenoidVoltageFault(ByteBuffer pcm_pointer, IntBuffer status) { return false; }
    public static void clearAllPCMStickyFaults(ByteBuffer pcm_pointer, IntBuffer status) {}
}
