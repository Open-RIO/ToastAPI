package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class RelayJNI extends DIOJNI {
    public static void setRelayForward(ByteBuffer digital_port_pointer, byte on, IntBuffer status) {
        SimulationData.setRelay(digital_port_pointer.get(0), true, on == 1);
    }

    public static void setRelayReverse(ByteBuffer digital_port_pointer, byte on, IntBuffer status) {
        SimulationData.setRelay(digital_port_pointer.get(0), false, on == 1);
    }

    public static byte getRelayForward(ByteBuffer digital_port_pointer, IntBuffer status) {
        return (byte) (SimulationData.relay_fwd[digital_port_pointer.get(0)] ? 1 : 0);
    }

    public static byte getRelayReverse(ByteBuffer digital_port_pointer, IntBuffer status) {
        return (byte) (SimulationData.relay_rvs[digital_port_pointer.get(0)] ? 1 : 0);
    }
}

