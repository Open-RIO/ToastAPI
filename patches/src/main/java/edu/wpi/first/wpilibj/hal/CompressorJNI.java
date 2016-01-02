package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class CompressorJNI extends JNIWrapper {
    public static long initializeCompressor(byte module) {
        SimulationData.setCompressor(module);
        return module;
    }

    public static boolean checkCompressorModule(byte module) { return true; }

    public static boolean getCompressor(long pcm_pointer) {
        return SimulationData.compressorRunning((byte)pcm_pointer);
    }

    public static void setClosedLoopControl(long pcm_pointer, boolean value) {
        SimulationData.setCompressorLoop((byte)pcm_pointer, value);
    }

    public static boolean getClosedLoopControl(long pcm_pointer) {
        return SimulationData.loop_compressors[(int)pcm_pointer];
    }

    public static boolean getPressureSwitch(long pcm_pointer) {
        return SimulationData.compressor_pressure[(int)pcm_pointer];
    }

    public static float getCompressorCurrent(long pcm_pointer) {
        return SimulationData.compressor_current[(int)pcm_pointer];
    }

    public static boolean getCompressorCurrentTooHighFault(long pcm_pointer) { return false; }
    public static boolean getCompressorCurrentTooHighStickyFault(long pcm_pointer) { return false; }
    public static boolean getCompressorShortedStickyFault(long pcm_pointer) { return false; }
    public static boolean getCompressorShortedFault(long pcm_pointer) { return false; }
    public static boolean getCompressorNotConnectedStickyFault(long pcm_pointer) { return false; }
    public static boolean getCompressorNotConnectedFault(long pcm_pointer) { return false; }
    public static void clearAllPCMStickyFaults(long pcm_pointer) {}
}
