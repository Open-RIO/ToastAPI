package jaci.openrio.toast.core.loader.simulation.jni;

import edu.wpi.first.wpilibj.hal.InterruptJNI;
import jaci.openrio.toast.core.ToastBootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class InterruptContainer {

    public static ArrayList<InterruptContainer> interrupts = new ArrayList<>();

    private int index;
    private InterruptJNI.InterruptJNIHandlerFunction func;
    private Object funcParam;
    private boolean triggerRising = true, triggerFalling = false;
    private boolean enabled = true;

    private long risingTime, fallingTime;

    private int pin;

    public InterruptContainer() {
        interrupts.add(this);
    }

    public void setPin(int p) {
        this.pin = p;
    }

    public int getPin() {
        return pin;
    }

    public static InterruptContainer getByPin(int p) {
        for (InterruptContainer cont : interrupts) {
            if (cont.pin == p) return cont;
        }
        return null;
    }

    public synchronized void waitForInterrupt(double timeout) {
        try {
            wait((long) (timeout * 1000));
        } catch (Exception e) {
        }
    }

    public void setEnabled(boolean s) {
        this.enabled = s;
    }

    public static InterruptContainer getByIndex(int index) {
        return interrupts.get(index);
    }

    public void setup(boolean triggerRising, boolean triggerFalling) {
        this.triggerRising = triggerRising;
        this.triggerFalling = triggerFalling;
    }

    public void setFunc(InterruptJNI.InterruptJNIHandlerFunction func, Object param) {
        this.func = func;
        this.funcParam = param;
    }

    public synchronized void trigger(boolean isRising) {
        if (!enabled)
            return;

        if (isRising)
            risingTime = System.currentTimeMillis();
        else
            fallingTime = System.currentTimeMillis();

        if (isRising && triggerRising || !isRising && triggerFalling) {
            if (func != null)
                func.apply(interrupts.indexOf(this), funcParam);

            notifyAll();
        }
    }

    public long getTime(boolean isRising) {
        long t = isRising ? risingTime : fallingTime;
        t -= ToastBootstrap.startTimeMS;
        return t / 1000;
    }

}
