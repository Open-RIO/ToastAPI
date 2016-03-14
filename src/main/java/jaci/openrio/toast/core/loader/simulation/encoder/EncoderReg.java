package jaci.openrio.toast.core.loader.simulation.encoder;

import java.util.HashMap;

/**
 * Registry for simulated encoders
 * 
 * @author LoadingPleaseWait
 */
public class EncoderReg {

	public static HashMap<Long, EncoderWrapper> wrappers = new HashMap<>();
	
	/**
	 * Wrapper for simulated encoders to be put in the EncoderRegistry
	 */
	public static class EncoderWrapper{
		
		private int value;
		private double rate;
		
		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}
		
		/**
		 * @param value the value to set
		 */
		public void setValue(int value) {
			this.value = value;
		}
		
		/**
		 * @return the rate
		 */
		public double getRate() {
			return rate;
		}
		
		/**
		 * @param rate the rate to set
		 */
		public void setRate(double rate) {
			this.rate = rate;
		}
		
	}

}
