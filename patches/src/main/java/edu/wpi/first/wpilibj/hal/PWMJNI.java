package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class PWMJNI extends DIOJNI {
	
	public static boolean allocatePWMChannel(ByteBuffer digital_port_pointer, IntBuffer status) {
		return true;
	}
	
	public static void freePWMChannel(ByteBuffer digital_port_pointer, IntBuffer status) {
	}
	
	public static void setPWM(ByteBuffer digital_port_pointer, short value, IntBuffer status) {
		byte port = digital_port_pointer.get(0);
		SimulationData.setPWM(port, (double) (value - 1012) / 255);
	}
	
	public static short getPWM(ByteBuffer digital_port_pointer, IntBuffer status) {
		return (short) (SimulationData.pwmValues[digital_port_pointer.get(0)] * 255 + 1012);
	}
	
	public static void latchPWMZero(ByteBuffer digital_port_pointer, IntBuffer status) {
	}
	
	public static void setPWMPeriodScale(ByteBuffer digital_port_pointer, int squelchMask, IntBuffer status) {
	}
	
	public static ByteBuffer allocatePWM(IntBuffer status) {
		return ByteBuffer.allocate(1);
	}
	
	public static void freePWM(ByteBuffer pwmGenerator, IntBuffer status) {
	}
	
	public static void setPWMRate(double rate, IntBuffer status) {
	}
	
	public static void setPWMDutyCycle(ByteBuffer pwmGenerator, double dutyCycle, IntBuffer status) {
	}
	
	public static void setPWMOutputChannel(ByteBuffer pwmGenerator, int pin, IntBuffer status) {
	}
	
}
