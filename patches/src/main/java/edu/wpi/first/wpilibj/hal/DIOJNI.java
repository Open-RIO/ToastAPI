package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

import java.nio.IntBuffer;

public class DIOJNI extends JNIWrapper {

	public static int initializeDigitalPort(int port_pointer, boolean input) {
		SimulationData.setDIODir((byte)port_pointer, input);
		return port_pointer;
	}

	public static boolean checkDIOChannel(int channel) {
		return true;
	}

	public static void freeDIO(int dioPortHandle) { }

	public static void setDIO(int digital_port_pointer, short value) {
		SimulationData.setDIO((byte)digital_port_pointer, (byte)value);
	}

	public static boolean getDIO(int digital_port_pointer) {
		return SimulationData.dioValues[digital_port_pointer] == 1;
	}

	public static boolean getDIODirection(int digital_port_pointer, IntBuffer status) {
		return SimulationData.dioDirections[digital_port_pointer] == 1;
	}

	public static void pulse(int dioPortHandle, double pulseLength) {
	}

	public static boolean isPulsing(int dioPortHandle) {
		return false;
	}

	public static boolean isAnyPulsing() {
		return false;
	}

	public static short getLoopTiming() {
		return 80;
	}

	public static int allocateDigitalPWM() { return 0; }

	public static void freeDigitalPWM(int pwmGenerator) { }

	public static void setDigitalPWMRate(double rate) { }

	public static void setDigitalPWMDutyCycle(int pwmGenerator, double dutyCycle) { }

	public static void setDigitalPWMOutputChannel(int pwmGenerator, int channel) { }
}
