package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class DIOJNI extends JNIWrapper {

	public static long initializeDigitalPort(long port_pointer) {
		return port_pointer;
	}

	public static boolean allocateDIO(long digital_port_pointer, boolean input) {
		SimulationData.setDIODir((byte)digital_port_pointer, input);
		return input;
	}

	public static void freeDIO(long digital_port_pointer) { }

	public static void setDIO(long digital_port_pointer, short value) {
		SimulationData.setDIO((byte)digital_port_pointer, (byte)value);
	}

	public static boolean getDIO(long digital_port_pointer) {
		return SimulationData.dioValues[(byte)digital_port_pointer] == 1;
	}

	public static boolean getDIODirection(long digital_port_pointer) {
		return SimulationData.dioDirections[(byte)digital_port_pointer] == 1;
	}

	public static void pulse(long digital_port_pointer, double pulseLength) {
		//TODO: Implement Pulse Control
	}

	public static byte isPulsing(long digital_port_pointer) {
		return 0;
	}

	public static byte isAnyPulsing() {
		return 0;
	}

	public static short getLoopTiming() {
		return 80;
	}

}
