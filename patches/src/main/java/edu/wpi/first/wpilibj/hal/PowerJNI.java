package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class PowerJNI extends JNIWrapper {
	
	public static float getVinVoltage() {
		return (float) SimulationData.powerVinVoltage;
	}
	
	public static float getVinCurrent() {
		return (float) SimulationData.powerVinCurrent;
	}
	
	public static float getUserVoltage6V() {
		return (float) SimulationData.powerUserVoltage6V;
	}
	
	public static float getUserCurrent6V() {
		return (float) SimulationData.powerUserCurrent6V;
	}
	
	public static boolean getUserActive6V() {
		return SimulationData.powerUserActive6V;
	}
	
	public static int getUserCurrentFaults6V() {
		return SimulationData.powerUserFaults6V;
	}

	public static float getUserVoltage5V() {
		return (float) SimulationData.powerUserVoltage5V;
	}
	
	public static float getUserCurrent5V() {
		return (float) SimulationData.powerUserCurrent5V;
	}
	
	public static boolean getUserActive5V() {
		return SimulationData.powerUserActive5V;
	}
	
	public static int getUserCurrentFaults5V() {
		return SimulationData.powerUserFaults5V;
	}

	public static float getUserVoltage3V3() {
		return (float) SimulationData.powerUserVoltage3V3;
	}
	
	public static float getUserCurrent3V3() {
		return (float) SimulationData.powerUserCurrent3V3;
	}
	
	public static boolean getUserActive3V3() {
		return SimulationData.powerUserActive3V3;
	}
	
	public static int getUserCurrentFaults3V3() {
		return SimulationData.powerUserFaults3V3;
	}
	
}
