package jaci.openrio.toast.core.monitoring.power;

public class PDPPoll {

    public double[] portCurrent = new double[16];
    public double totalCurrentDraw;
    double highestCurrent;
    public int highestIndex = 0;

    public double batteryVoltage;

    public PDPPoll(double voltage, double... currents) {
        for (int i = 0; i < currents.length; i++) {
            portCurrent[i] = currents[i];
            totalCurrentDraw += currents[i];

            if (currents[i] > highestCurrent) {
                highestCurrent = currents[i];
                highestIndex = i;
            }
        }
        batteryVoltage = voltage;
    }

}
