package edu.wpi.first.wpilibj.hal;

import edu.wpi.first.wpilibj.Utility;
import jaci.openrio.toast.core.loader.simulation.SimulationData;

public class NotifierJNI extends JNIWrapper {

    public static long initializeNotifier(Runnable func) {
        for (int i = 0; i < 10; i++) {
            if (SimulationData.notifiers[i] == null) {
                SimulationData.notifiers[i] = func;
                SimulationData.notifierTriggerTimes[i] = -1;
                return i;
            }
        }
        return 0;
    }

    public static void cleanNotifier(long notifierPtr) {
        SimulationData.notifiers[(int)notifierPtr] = null;
        SimulationData.notifierTriggerTimes[(int)notifierPtr] = -1;
    }

    public static void updateNotifierAlarm(long notifierPtr, int triggerTime) {
        SimulationData.notifierTriggerTimes[(int)notifierPtr] = triggerTime / 1000;     // Why microseconds? It's such an awkward unit of measurement
        SimulationData.notifierUpdate();
    }
}
