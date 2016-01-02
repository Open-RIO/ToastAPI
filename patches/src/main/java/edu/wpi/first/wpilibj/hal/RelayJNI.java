package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class RelayJNI extends DIOJNI {
    public static void setRelayForward(long digital_port_pointer, byte on) {
        SimulationData.setRelay((byte)digital_port_pointer, true, on == 1);
    }

    public static void setRelayReverse(long digital_port_pointer, byte on) {
        SimulationData.setRelay((byte)digital_port_pointer, false, on == 1);
    }

    public static byte getRelayForward(long digital_port_pointer) {
        return (byte) (SimulationData.relay_fwd[(int)digital_port_pointer] ? 1 : 0);
    }

    public static byte getRelayReverse(long digital_port_pointer) {
        return (byte) (SimulationData.relay_rvs[(int)digital_port_pointer] ? 1 : 0);
    }
}

