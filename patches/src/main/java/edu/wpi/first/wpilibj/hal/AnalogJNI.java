package edu.wpi.first.wpilibj.hal;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

/**
 * Analog Input/Output
 */
public class AnalogJNI extends JNIWrapper {

	public static boolean checkAnalogInputChannel(int pin){
		return true;
	}
	
	public static boolean checkModule(byte module){
		return true;
	}
	
	public static boolean checkAnalogOuputChannel(int pin){
		return true;
	}
	
	public static void cleanAnalogTrigger(long analog_trigger_pointer) {
		
	}
	
	public static void freeAnalogInputPort(long port_pointer){
		
	}
	
	public static void freeAnalogOutputPort(long port_pointer){
		
	}

	public static int getAccumulatorCount(long analog_port_pointer){
		return 0;
	}
	
	public static void getAccumulatorOutput(long analog_port_pointer, LongBuffer value, IntBuffer count){
		
	}
	
	public static long getAccumulatorValue(long analog_port_pointer){
		return analog_port_pointer;
	}
	
	public static int getAnalogAverageBits(long analog_port_pointer){
		return 0;
	}
	
	public static int getAnalogAverageValue(long analog_port_pointer){
		return 0;
	}
	
	public static double getAnalogAverageVoltage(long analog_port_pointer){
		return analog_port_pointer;
	}
	
	public static int getAnalogLSBWeight(long analog_port_pointer){
		return 0;
	}
	
	public static int getAnalogOffset(long analog_port_pointer){
		return 0;
	}

	public static double getAnalogOutput(long port_pointer){
		return port_pointer;
	}
	
	public static int getAnalogOversampleBits(long analog_port_pointer){
		return 0;
	}
	
	public static double getAnalogSampleRate(){
		// Everything fucking dies if this is set to 0. resetAccumulator() in AnalogInput runs 1.0 / getAnalogSampleRate(),
		// which means 1.0 / 0 in floating point arithmetic, which equals Infinity, hence blocking the main thread indefinitely.
		// 1 / 0 gives an exception, but 1.0 / 0 gives infinity. Welcome to floating point arithmetic.
		return 1 / 25d;
	}
	
	public static boolean getAnalogTriggerInWindow(long analog_trigger_pointer){
		return false;
	}
	
	public static boolean getAnalogTriggerOutput(long analog_trigger_pointer, int type){
		return false;
	}
	
	public static boolean getAnalogTriggerTriggerState(long analog_trigger_pointer){
		return false;
	}
	
	public static short getAnalogValue(long analog_port_pointer){
		return 0;
	}
	
	public static double getAnalogVoltage(long analog_port_pointer){
		return analog_port_pointer;
	}
	
	public static int getAnalogVoltsToValue(long analog_port_pointer, double voltage){
		return 0;
	}
	
	public static void initAccumulator(long analog_port_pointer){
		
	}
	
	public static long initializeAnalogInputPort(long port_pointer){
		return port_pointer;
	}
	
	public static long initializeAnalogOutputPort(long port_pointer){
		return port_pointer;
	}
	
	public static long initializeAnalogTrigger(long port_pointer, IntBuffer index){
		return port_pointer;
	}
	
	public static boolean isAccumulatorChannel(long analog_port_pointer){
		return false;
	}
	
	public static void resetAccumulator(long analog_port_pointer){
		
	}
	
	public static void setAccumulatorCenter(long analog_port_pointer, int center){
		
	}
	
	public static void setAccumulatorDeadband(long analog_port_pointer, int deadband){
		
	}
	
	public static void setAnalogAverageBits(long analog_port_pointer, int bits){
		
	}
	
	public static void setAnalogOutput(long port_pointer, double voltage){
		
	}
	
	public static void setAnalogOversampleBits(long analog_port_pointer, int bits){
		
	}
	
	public static void setAnalogSampleRate(double samplesPerSecond){
		
	}
	
	public static void setAnalogTriggerAveraged(long analog_trigger_pointer, boolean useAveragedValue){
		
	}
	
	public static void setAnalogTriggerFiltered(long analog_trigger_pointer, boolean useFilteredValue){
		
	}
	
	public static void setAnalogTriggerLimitsRaw(long analog_trigger_pointer, int lower, int upper){
		
	}
	
	public static void setAnalogTriggerLimitsVoltage(long analog_trigger_pointer, double lower, double upper){
		
	}
}
