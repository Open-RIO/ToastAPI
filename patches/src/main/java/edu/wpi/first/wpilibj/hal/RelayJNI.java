package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class RelayJNI extends DIOJNI {
    public static int initializeRelayPort(int halPortHandle, boolean forward) {
        return halPortHandle * 2 + (forward ? 1 : 0);       // Rvs = 0, 2, 4, 6   Fwd = 1, 3, 5, 7
    }

    public static void freeRelayPort(int relayPortHandle) { }

    public static boolean checkRelayChannel(int channel) {
        return true;
    }

    public static void setRelay(int relayPortHandle, boolean on) {
        int port = relayPortHandle;
        boolean fwd = false;
        if (port % 2 != 0) {
            port -= 1;
            fwd = true;
        }
        port /= 2;
        if (fwd) {
            SimulationData.relay_fwd[port] = on;
        } else {
            SimulationData.relay_rvs[port] = on;
        }
    }

    public static boolean getRelay(int relayPortHandle) {
        int port = relayPortHandle;
        boolean fwd = false;
        if (port % 2 != 0) {
            port -= 1;
            fwd = true;
        }
        port /= 2;
        return fwd ? SimulationData.relay_fwd[port] : SimulationData.relay_rvs[port];
    }
}

