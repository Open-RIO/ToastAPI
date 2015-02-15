package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

import java.nio.IntBuffer;

public class PDPJNI extends JNIWrapper {

	public static double getPDPTemperature(IntBuffer status) {
		return SimulationData.pdpTemperature;
	}

	public static double getPDPVoltage(IntBuffer status) {
		return SimulationData.pdpVoltage;
	}

	public static double getPDPChannelCurrent(byte channel, IntBuffer status) {
		return SimulationData.pdpChannelCurrent[channel];
	}

	public static double getPDPTotalCurrent(IntBuffer status) {
		double total = 0D;
		for (double d : SimulationData.pdpChannelCurrent)
			total += d;
		return total;
	}

	public static double getPDPTotalPower(IntBuffer status) {
		return 0;
	}

	public static double getPDPTotalEnergy(IntBuffer status) {
		return 0;
	}

	public static void resetPDPTotalEnergy(IntBuffer status) { }

	public static void clearPDPStickyFaults(IntBuffer status) { }

}