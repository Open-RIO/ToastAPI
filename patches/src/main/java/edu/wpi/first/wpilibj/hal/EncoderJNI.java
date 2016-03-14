package edu.wpi.first.wpilibj.hal;

import java.nio.IntBuffer;

import jaci.openrio.toast.core.loader.simulation.encoder.EncoderReg;

/**
 * JNIWrapper for encoders
 */
public class EncoderJNI extends JNIWrapper {

    public static long initializeEncoder(byte port_a_module, int port_a_pin,
              boolean port_a_analog_trigger, byte port_b_module, int port_b_pin, boolean port_b_analog_trigger,
              boolean reverseDirection, IntBuffer index){
    	long pointer = EncoderReg.wrappers.size();
    	EncoderReg.wrappers.put(pointer, new EncoderReg.EncoderWrapper());
        return pointer;
    }
    
    public static void freeEncoder(long encoder_pointer){
    }
    
    public static void resetEncoder(long encoder_pointer){
    }
    
    public static int getEncoder(long encoder_pointer){
        return 4 * EncoderReg.wrappers.get(encoder_pointer).getValue();
    }
    
    public static double getEncoderPeriod(long encoder_pointer){
        return 1 / EncoderReg.wrappers.get(encoder_pointer).getRate();
    }
    
    public static void setEncoderMaxPeriod(long encoder_pointer, double maxPeriod){
    }
    
    public static boolean getEncoderStopped(long encoder_pointer){
        return false;
    }
    
    public static boolean getEncoderDirection(long encoder_pointer){
        return false;
    }
    
    public static void setEncoderReverseDirection(long encoder_pointer,
              boolean reverseDirection){
    }
    
    public static void setEncoderSamplesToAverage(long encoder_pointer,
              int samplesToAverage){
    }
    
    public static int getEncoderSamplesToAverage(long encoder_pointer){
        return 0;
    }
    
    public static void setEncoderIndexSource(long digital_port, int pin,
              boolean analogTrigger, boolean activeHigh, boolean edgeSensitive){
    }

}
