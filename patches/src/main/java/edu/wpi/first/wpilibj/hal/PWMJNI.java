package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

import edu.wpi.first.wpilibj.PWMConfigDataResult;
import jaci.openrio.toast.core.loader.simulation.SimulationData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.IntBuffer;
import java.util.function.Function;

public class PWMJNI extends DIOJNI {
	
	public static boolean initializePWMPort(int digital_port_pointer) {
		return true;
	}

	public static boolean checkPWMChannel(int channel) {
		return true;
	}

	public static void freePWMChannel(int digital_port_pointer) {
	}

	public static void setPWMConfigRaw(int pwmPortHandle, int maxPwm,
											  int deadbandMaxPwm, int centerPwm,
											  int deadbandMinPwm, int minPwm) {
		try {
			Constructor<PWMConfigDataResult> constructor = PWMConfigDataResult.class.getDeclaredConstructor(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);
			constructor.setAccessible(true);
			PWMConfigDataResult result = constructor.newInstance(maxPwm, deadbandMaxPwm, centerPwm, deadbandMinPwm, minPwm);
			SimulationData.pwmConfigs[pwmPortHandle] = result;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) { }
	}

	public static void setPWMConfig(int pwmPortHandle, double maxPwm,
										   double deadbandMaxPwm, double centerPwm,
										   double deadbandMinPwm, double minPwm) {
		try {
			double loopTime = DIOJNI.getLoopTiming() / (ConstantsJNI.getSystemClockTicksPerMicrosecond() * 1e3);
			Function<Double, Integer> convert = (a) -> { return (int)((a - 1.5) / loopTime + 999); };
			Constructor<PWMConfigDataResult> constructor = PWMConfigDataResult.class.getDeclaredConstructor(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);
			constructor.setAccessible(true);
			PWMConfigDataResult result = constructor.newInstance(convert.apply(maxPwm), convert.apply(deadbandMaxPwm),
					convert.apply(centerPwm), convert.apply(deadbandMinPwm), convert.apply(minPwm));
			SimulationData.pwmConfigs[pwmPortHandle] = result;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) { }
	}

	public static PWMConfigDataResult getPWMConfigRaw(int pwmPortHandle) {
		return SimulationData.pwmConfigs[pwmPortHandle];					// This function is never called, so I'm going to leave this for now
	}

	public static void setPWMEliminateDeadband(int pwmPortHandle, boolean eliminateDeadband) {
		SimulationData.pwmEliminateDeadband[pwmPortHandle] = eliminateDeadband;
	}

	public static boolean getPWMEliminateDeadband(int pwmPortHandle) {
		return SimulationData.pwmEliminateDeadband[pwmPortHandle];
	}

	public static double clamp(double val, double min, double max) {
		if (val < min) val = min;
		else if (val > max) val = max;
		return val;
	}

	public static double minPositive(int pwmPortHandle) {
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		boolean deadbandElim = getPWMEliminateDeadband(pwmPortHandle);
		return deadbandElim ? config.deadbandMax : config.center + 1;
	}

	public static double maxNegative(int pwmPortHandle) {
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		boolean deadbandElim = getPWMEliminateDeadband(pwmPortHandle);
		return deadbandElim ? config.deadbandMin : config.center - 1;
	}

	public static double posScaleFactor(int pwmPortHandle) {
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		double max = config.max;
		double min = minPositive(pwmPortHandle);
		return max - min;
	}

	public static double negScaleFactor(int pwmPortHandle) {
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		double max = maxNegative(pwmPortHandle);
		double min = config.min;
		return max - min;
	}

	public static double fullScaleFactor(int pwmPortHandle) {
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		boolean deadbandElim = getPWMEliminateDeadband(pwmPortHandle);
		return config.max - config.min;
	}

	public static void setPWMRaw(int digital_port_pointer, short value) {
		SimulationData.setPWM((byte)digital_port_pointer, (double) (value - 1012) / 255);
	}

	public static void setPWMSpeed(int pwmPortHandle, double speed) {
		speed = clamp(speed, -1.0, 1.0);
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		int raw;
		if (speed == 0.0) raw = config.center;
		else if (speed > 0.0) {
			raw = (int)(speed * ((double) posScaleFactor(pwmPortHandle)) + ((double) minPositive(pwmPortHandle)) + 0.5);
		} else {
			raw = (int)(speed * ((double) negScaleFactor(pwmPortHandle)) + ((double) maxNegative(pwmPortHandle)) + 0.5);
		}
		setPWMRaw(pwmPortHandle, (short)raw);
	}

	public static void setPWMPosition(int pwmPortHandle, double position) {
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		position = clamp(position, 0.0, 1.0);
		setPWMRaw(pwmPortHandle, (short) ((position * (double) fullScaleFactor(pwmPortHandle)) + config.min));
	}
	
	public static short getPWMRaw(int digital_port_pointer) {
		return (short) (SimulationData.pwmValues[(byte)digital_port_pointer] * 255 + 1012);
	}

	public static double getPWMSpeed(int pwmPortHandle) {
		int val = getPWMRaw(pwmPortHandle);
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		if (val > config.max) return 1.0;
		if (val < config.min) return -1.0;
		if (val > minPositive(pwmPortHandle)) {
			return (double) (val - minPositive(pwmPortHandle)) / (double) posScaleFactor(pwmPortHandle);
		}
		if (val < maxNegative(pwmPortHandle)) {
			return (double) (val - maxNegative(pwmPortHandle)) / (double) negScaleFactor(pwmPortHandle);
		}
		return 0.0;
	}

	public static double getPWMPosition(int pwmPortHandle) {
		int val = getPWMRaw(pwmPortHandle);
		PWMConfigDataResult config = getPWMConfigRaw(pwmPortHandle);
		if (val > config.max) return 1.0;
		if (val < config.min) return 0.0;
		return (double) (val - config.min) / (double) fullScaleFactor(pwmPortHandle);
	}

	public static void setPWMDisabled(int pwmPortHandle) {
		setPWMRaw(pwmPortHandle, (short)0);
	}

	public static void latchPWMZero(int pwmPortHandle) { }

	public static void setPWMPeriodScale(int pwmPortHandle, int squelchMask) { }
	
}
