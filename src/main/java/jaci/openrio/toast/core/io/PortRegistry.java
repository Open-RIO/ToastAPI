package jaci.openrio.toast.core.io;

import edu.wpi.first.wpilibj.*;

import java.util.ArrayList;

/**
 * A registry for the ports of the RoboRIO. This allows for controlling ports on a more general scale. This
 * allows for things like cutting all motors in the event of an 'un-official e-stop'. This is also used for
 * monitoring purposes.
 *
 * @author Jaci
 */
public class PortRegistry {

    static ArrayList<PortContainer> ports;

    /**
     * Initialize the Registry. This should NOT be triggered by anything except for {@link jaci.openrio.toast.core.Toast}
     */
    public static void init() {
        ports = new ArrayList<PortContainer>();
    }

    /**
     * Register a port with the given container
     */
    public static void registerPort(PortContainer port) {
        ports.add(port);
    }

    /**
     * Register a port and create the appropriate container for the port ID specified
     * @param type The type of port to register
     * @param portID The ID of the port (where it is connected on the RoboRIO
     * @param portInstance The instance of the port (the sensor)
     * @return A new {@link jaci.openrio.toast.core.io.PortContainer} for the port
     */
    public static PortContainer registerPort(PortType type, int portID, SensorBase portInstance) {
        PortContainer container = new PortContainer(type, portID, portInstance);
        ports.add(container);
        return container;
    }

    /**
     * Register a port and create the instance as well as the container for the ID and type specified
     * @param type The type of port to create
     * @param portID The ID of the port (where it is connected on the RoboRIO)
     * @return The new {@link jaci.openrio.toast.core.io.PortContainer} object for the port. This also contains the
     *          {@link edu.wpi.first.wpilibj.SensorBase} for the port
     */
    public static PortContainer registerPort(PortType type, int portID) {
        SensorBase base = null;
        switch (type) {
            case PWM:
                base = new PWM(portID);
                break;
            case ANALOG_IN:
                base = new AnalogInput(portID);
                break;
            case DIGITAL_IN:
                base = new DigitalInput(portID);
                break;
            case DIGITAL_OUT:
                base = new DigitalOutput(portID);
                break;
            case RELAY:
                base = new Relay(portID);
                break;
        }
        PortContainer container = new PortContainer(type, portID, base);
        registerPort(container);
        return container;
    }

    /**
     * Get a port object
     * @param type The type of port
     * @param portID The port ID (where it is connected on the RoboRIO)
     * @return The {@link jaci.openrio.toast.core.io.PortContainer} found, or 'null' if no
     *          port is registered matching the parameters
     */
    public static PortContainer getPort(PortType type, int portID) {
        for (PortContainer container : ports) {
            if (container.type == type && container.id == portID)
                return container;
        }
        return null;
    }

}
