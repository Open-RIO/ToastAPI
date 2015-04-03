package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.loader.simulation.SimulationData;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class HALUtil extends JNIWrapper {
	public static final int NULL_PARAMETER = -1005;
	public static final int SAMPLE_RATE_TOO_HIGH = 1001;
	public static final int VOLTAGE_OUT_OF_RANGE = 1002;
	public static final int LOOP_TIMING_ERROR = 1004;
	public static final int INCOMPATIBLE_STATE = 1015;
	public static final int ANALOG_TRIGGER_PULSE_OUTPUT_ERROR = -1011;
	public static final int NO_AVAILABLE_RESOURCES = -104;
	public static final int PARAMETER_OUT_OF_RANGE = -1028;

	public static ByteBuffer initializeMutexNormal() {
		return ByteBuffer.allocate(1);
	}
	public static void deleteMutex(ByteBuffer sem) {
	}
	public static ByteBuffer initializeMultiWait() {
		return ByteBuffer.allocate(1);
	}
	public static void deleteMultiWait(ByteBuffer sem) {
	}

	public static byte takeMultiWait(ByteBuffer sem, ByteBuffer m, int timeOut) {
		try {
			Thread.sleep(20);
		} catch (Exception e) {}
		return 0;
	}

	public static short getFPGAVersion(IntBuffer status) {
		return 2009;
	}

	public static int getFPGARevision(IntBuffer status) {
		return 0;
	}

	public static long getFPGATime(IntBuffer status) {
		return ToastBootstrap.startTimeNS - System.nanoTime();
	}

	public static boolean getFPGAButton(IntBuffer status) {
		return SimulationData.userButtonPressed;
	}

	public static String getHALErrorMessage(int code) {
		return "";
	}
	
	public static int getHALErrno() {
		return 0;
	}
	public static String getHALstrerror(int errno) {
		return "";
	}
	public static String getHALstrerror(){
		return getHALstrerror(getHALErrno());
	}

	public static void checkStatus(IntBuffer status) {
	}

}
