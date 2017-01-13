package edu.wpi.first.wpilibj.hal;

import com.sun.tools.javadoc.Start;
import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class CompressorJNI extends JNIWrapper {
    public static int initializeCompressor(byte module) {
        SimulationData.setCompressor(module);
        return getPort(module);
    }

    public static boolean checkCompressorModule(byte module) { return true; }

    public static boolean getCompressor(int pcm_pointer) {
        return SimulationData.compressorRunning((byte)pcm_pointer);
    }

    public static void setClosedLoopControl(int pcm_pointer, boolean value) {
        SimulationData.setCompressorLoop((byte)pcm_pointer, value);
    }

    public static boolean getClosedLoopControl(int pcm_pointer) {
        return SimulationData.loop_compressors[pcm_pointer];
    }

    public static boolean getPressureSwitch(int pcm_pointer) {
        return SimulationData.compressor_pressure[pcm_pointer];
    }

    public static double getCompressorCurrent(int pcm_pointer) {
        return SimulationData.compressor_current[pcm_pointer];
    }

    public static boolean getCompressorCurrentTooHighFault(int pcm_pointer) { return false; }
    public static boolean getCompressorCurrentTooHighStickyFault(int pcm_pointer) { return false; }
    public static boolean getCompressorShortedStickyFault(int pcm_pointer) { return false; }
    public static boolean getCompressorShortedFault(int pcm_pointer) { return false; }
    public static boolean getCompressorNotConnectedStickyFault(int pcm_pointer) { return false; }
    public static boolean getCompressorNotConnectedFault(int pcm_pointer) { return false; }
    public static void clearAllPCMStickyFaults(int pcm_pointer) {}
}
