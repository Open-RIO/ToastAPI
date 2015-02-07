package jaci.openrio.toast.core.io;

import edu.wpi.first.wpilibj.*;

/**
 * Enumeration of port types on the RoboRIO, to avoid PortID conflicts and cast to the appropriate
 * {@link edu.wpi.first.wpilibj.SensorBase} sub-class
 *
 * @author Jaci
 */
public enum PortType {

    PWM(edu.wpi.first.wpilibj.PWM.class),
    ANALOG_IN(AnalogInput.class),
    DIGITAL_IN(DigitalInput.class),
    DIGITAL_OUT(DigitalOutput.class),
    RELAY(Relay.class);

    Class<? extends SensorBase> sensorClass;

    PortType(Class<? extends SensorBase> classCast) {
        this.sensorClass = classCast;
    }

    public Class<? extends SensorBase> getClassType() {
        return sensorClass;
    }

}
