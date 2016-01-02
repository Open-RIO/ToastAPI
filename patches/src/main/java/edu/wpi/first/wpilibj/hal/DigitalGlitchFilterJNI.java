package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class DigitalGlitchFilterJNI extends JNIWrapper {

    //TODO implement this into the DIOJNI

    public static void setFilterSelect(long digital_port_pointer, int filter_index) {
        SimulationData.glitchFilters[(int)digital_port_pointer] = filter_index;
    }

    public static int getFilterSelect(long digital_port_pointer) {
        return SimulationData.glitchFilters[(int) digital_port_pointer];
    }

    public static void setFilterPeriod(int filter_index, int fpga_cycles) {
        SimulationData.filterDurations[filter_index] = fpga_cycles;
    }

    public static int getFilterPeriod(int filter_index) {
        return SimulationData.filterDurations[filter_index];
    }
}
