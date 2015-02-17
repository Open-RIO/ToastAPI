package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class AccelerometerJNI extends JNIWrapper {

    public static void setAccelerometerActive(boolean active) {
        SimulationData.accelerometerEnabled = active;
    }

    public static void setAccelerometerRange(int range) {
        SimulationData.accelerometerRange = range;
    }

    public static double getAccelerometerX() {
        return SimulationData.accelerometer[0];
    }

    public static double getAccelerometerY() {
        return SimulationData.accelerometer[1];
    }

    public static double getAccelerometerZ() {
        return SimulationData.accelerometer[2];
    }

}
