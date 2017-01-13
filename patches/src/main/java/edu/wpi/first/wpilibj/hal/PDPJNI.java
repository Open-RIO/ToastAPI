package edu.wpi.first.wpilibj.hal;

import com.sun.tools.javadoc.Start;
import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class PDPJNI extends JNIWrapper {

	// Yes, these all link to a single index in SimulationData. No team in their right mind would attach
	// multiple Power Distribution Panels to their Robot. Is that even allowed?

	public static void initializePDP(int module) { }

	public static boolean checkPDPModule(int module) { return true; }

	public static boolean checkPDPChannel(int channel) { return true; }

	public static double getPDPTemperature(int module) {
		return SimulationData.pdpTemperature;
	}

	public static double getPDPVoltage(int module) {
		return SimulationData.pdpVoltage;
	}

	public static double getPDPChannelCurrent(byte channel, int module) {
		return SimulationData.pdpChannelCurrent[channel];
	}

	public static double getPDPTotalCurrent(int module) {
		double total = 0D;
		for (double d : SimulationData.pdpChannelCurrent)
			total += d;
		return total;
	}

	public static double getPDPTotalPower(int module) {
		return 0;
	}

	public static double getPDPTotalEnergy(int module) {
		return 0;
	}

	public static void resetPDPTotalEnergy(int module) { }

	public static void clearPDPStickyFaults(int module) { }

}
