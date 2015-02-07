package jaci.openrio.toast.core.io;

import edu.wpi.first.wpilibj.*;

/**
 * A container class for the {@link edu.wpi.first.wpilibj.SensorBase} object. This is used to keep track of the
 * {@link jaci.openrio.toast.core.io.PortType}, as well as the port ID and the {@link edu.wpi.first.wpilibj.SensorBase}
 * object. This allows for easy storage in Lists, Arrays and Maps, as well as keeping things generalized with easy
 * casting
 *
 * @author Jaci
 */
public class PortContainer {

    PortType type;
    int id;
    SensorBase base;

    /**
     * Create a new PortContainer object
     * @param type The type of port. This allows for casting as well as access through the WebUI
     * @param id The port ID. This is the port number the sensor is connected to on the RoboRIO
     * @param sensor The sensor object. This is the actual sensor instance, such as the
     *               {@link edu.wpi.first.wpilibj.DigitalInput} or {@link edu.wpi.first.wpilibj.PWM} for
     *               example
     */
    public PortContainer(PortType type, int id, SensorBase sensor) {
        this.type = type;
        this.id = id;
        this.base = sensor;
    }

    /**
     * Returns the port type bound to this container. {@link jaci.openrio.toast.core.io.PortType}
     * @return port type
     */
    public PortType getType() {
        return type;
    }

    /**
     * Returns the port ID of the sensor. This is the port the sensor is connected to on the RoboRIO
     * @return the port id
     */
    public int getPortID() {
        return id;
    }

    /**
     * Get the instance of the port. This is a generic return, returning {@link edu.wpi.first.wpilibj.SensorBase} to
     * be casted later for use
     * @return The instance of this port/sensor
     */
    public SensorBase getSensorBase() {
        return base;
    }

    /* RETURNS */

    public DigitalInput getDigitalInput() {
        if (type != PortType.DIGITAL_IN)
            throw new IllegalArgumentException("Unsupported Port Type. Needed: DIGITAL_IN. Given: " + type.name().toUpperCase());
        return (DigitalInput) getSensorBase();
    }

    public DigitalOutput getDigitalOutput() {
        if (type != PortType.DIGITAL_OUT)
            throw new IllegalArgumentException("Unsupported Port Type. Needed: DIGITAL_OUT. Given: " + type.name().toUpperCase());
        return (DigitalOutput) getSensorBase();
    }

    public AnalogInput getAnalogInput() {
        if (type != PortType.ANALOG_IN)
            throw new IllegalArgumentException("Unsupported Port Type. Needed: ANALOG_IN. Given: " + type.name().toUpperCase());
        return (AnalogInput) getSensorBase();
    }

    public PWM getPWM() {
        if (type != PortType.PWM)
            throw new IllegalArgumentException("Unsupported Port Type. Needed: PWM. Given: " + type.name().toUpperCase());
        return (PWM) getSensorBase();
    }

    public Relay getRelay() {
        if (type != PortType.RELAY)
            throw new IllegalArgumentException("Unsupported Port Type. Needed: RELAY. Given: " + type.name().toUpperCase());
        return (Relay) getSensorBase();
    }



}
