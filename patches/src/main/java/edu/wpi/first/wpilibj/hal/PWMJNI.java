package edu.wpi.first.wpilibj.hal;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class PWMJNI extends DIOJNI {
	
	public static boolean allocatePWMChannel(long digital_port_pointer) {
		return true;
	}
	
	public static void freePWMChannel(long digital_port_pointer) {
	}
	
	public static void setPWM(long digital_port_pointer, short value) {
		byte port = (byte)digital_port_pointer;
		double output = (double) (value - 1012) / 255;
		// let's figure out what motor controller is being used
		switch (SimulationData.motorControllers[port]){
		case FRCNetworkCommunicationsLibrary.tResourceType.kResourceType_VictorSP:
			if (output > 0.6)
				output += 0.01;
			else if (output < -0.8)
				output -= 0.01;
			output += 0.05;
			break;
		}
		SimulationData.setPWM(port, output);
	}
	
	public static short getPWM(long digital_port_pointer) {
		return (short) (SimulationData.pwmValues[(int)digital_port_pointer] * 255 + 1012);
	}
	
	public static void latchPWMZero(long digital_port_pointer) {
	}
	
	public static void setPWMPeriodScale(long digital_port_pointer, int squelchMask) {
	}
	
	public static long allocatePWM() {
		return 0L;			// It's okay, this is only used for freePWM, setPWMDutyCycle and setPWMOutputChannel. Everything else (digital_port_pointer) is the actual port number.
	}
	
	public static void freePWM(long pwmGenerator) {
	}
	
	public static void setPWMRate(double rate) {
	}
	
	public static void setPWMDutyCycle(long pwmGenerator, double dutyCycle) {
	}
	
	public static void setPWMOutputChannel(long pwmGenerator, int pin) {
	}
	
}
