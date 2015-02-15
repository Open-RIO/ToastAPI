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
        return SimulationData.accelerometerX;
    }

    public static double getAccelerometerY() {
        return SimulationData.accelerometerY;
    }

    public static double getAccelerometerZ() {
        return SimulationData.accelerometerZ;
    }

}
