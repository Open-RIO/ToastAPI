package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DIOJNI extends JNIWrapper {

	public static ByteBuffer initializeDigitalPort(ByteBuffer port_pointer, IntBuffer status) {
		return port_pointer;
	}

	public static byte allocateDIO(ByteBuffer digital_port_pointer, byte input, IntBuffer status) {
		SimulationData.setDIODir(digital_port_pointer.get(0), input);
		return input;
	}

	public static void freeDIO(ByteBuffer digital_port_pointer, IntBuffer status) {
	}

	public static void setDIO(ByteBuffer digital_port_pointer, short value, IntBuffer status) {
		SimulationData.setDIO(digital_port_pointer.get(0), (byte)value);
	}

	public static byte getDIO(ByteBuffer digital_port_pointer, IntBuffer status) {
		return SimulationData.dioValues[digital_port_pointer.get(0)];
	}

	public static byte getDIODirection(ByteBuffer digital_port_pointer, IntBuffer status) {
		return SimulationData.dioDirections[digital_port_pointer.get(0)];
	}

	public static void pulse(ByteBuffer digital_port_pointer, double pulseLength, IntBuffer status) {
		//TODO: Implement Pulse Control
	}

	public static byte isPulsing(ByteBuffer digital_port_pointer, IntBuffer status) {
		return 0;
	}

	public static byte isAnyPulsing(IntBuffer status) {
		return 0;
	}

	public static short getLoopTiming(IntBuffer status) {
		return 80;
	}

}
