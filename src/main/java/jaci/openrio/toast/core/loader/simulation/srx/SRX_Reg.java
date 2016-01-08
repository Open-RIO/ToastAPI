package jaci.openrio.toast.core.loader.simulation.srx;

import edu.wpi.first.wpilibj.CANTalon;

import java.util.HashMap;

/**
 * SWIG Registry for Simulated Talon SRXs over the CAN bus.
 *
 * @author Jaci
 */
public class SRX_Reg {

    public static HashMap<Long, SRX_Wrapper> wrappers = new HashMap<>();

    public static class SRX_Wrapper {

        public int dev_id, control, mode, slotid = 0;
        public double pvbus, voltage;
        public int follower, speed, position;

        public double[][] gains = new double[2][4];

        public HashMap<Integer, Double> params = new HashMap<>();

        public int cache_value = -1;

        Runnable updater;

        public SRX_Wrapper(int device_id, int control_period) {
            this.dev_id = device_id;
            this.control = control_period;
        }

        public void update() {
            if (updater != null) updater.run();
        }

        public void onUpdate(Runnable run) {
            updater = run;
        }

        public void setMode(int mode) {
            this.mode = mode;
            if (cache_value != -1)
                setDemand(cache_value);
            update();
        }

        public void setSlotID(int slot) {
            this.slotid = slot;
            update();
        }

        public void setVBus(double val) {
            this.pvbus = val;
            update();
        }

        public void setGains(int slot, int index, double value) {
            gains[slot][index] = value;
            update();
        }

        public void setDemand(int value) {
            cache_value = -1;
            if (mode == CANTalon.TalonControlMode.Voltage.value)
                voltage = (double)value / 256;
            else if (mode == CANTalon.TalonControlMode.Follower.value)
                follower = value;
            else if (mode == CANTalon.TalonControlMode.Position.value)
                position = value;
            else if (mode == CANTalon.TalonControlMode.Speed.value)
                speed = value;
            else if (mode == CANTalon.TalonControlMode.Disabled.value)
                cache_value = value;

            update();
        }

    }


}
