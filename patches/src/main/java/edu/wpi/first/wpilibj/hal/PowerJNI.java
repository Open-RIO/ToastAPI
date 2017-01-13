package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class PowerJNI extends JNIWrapper {

	public static double getVinVoltage() {
		return (double) SimulationData.powerVinVoltage;
	}
	
	public static double getVinCurrent() {
		return (double) SimulationData.powerVinCurrent;
	}
	
	public static double getUserVoltage6V() {
		return (double) SimulationData.powerUserVoltage6V;
	}
	
	public static double getUserCurrent6V() {
		return (double) SimulationData.powerUserCurrent6V;
	}
	
	public static boolean getUserActive6V() {
		return SimulationData.powerUserActive6V;
	}
	
	public static int getUserCurrentFaults6V() {
		return SimulationData.powerUserFaults6V;
	}

	public static double getUserVoltage5V() {
		return (double) SimulationData.powerUserVoltage5V;
	}
	
	public static double getUserCurrent5V() {
		return (double) SimulationData.powerUserCurrent5V;
	}
	
	public static boolean getUserActive5V() {
		return SimulationData.powerUserActive5V;
	}
	
	public static int getUserCurrentFaults5V() {
		return SimulationData.powerUserFaults5V;
	}

	public static double getUserVoltage3V3() {
		return (double) SimulationData.powerUserVoltage3V3;
	}
	
	public static double getUserCurrent3V3() {
		return (double) SimulationData.powerUserCurrent3V3;
	}
	
	public static boolean getUserActive3V3() {
		return SimulationData.powerUserActive3V3;
	}
	
	public static int getUserCurrentFaults3V3() {
		return SimulationData.powerUserFaults3V3;
	}
	
}
