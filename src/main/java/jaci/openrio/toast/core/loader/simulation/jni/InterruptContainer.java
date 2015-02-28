package jaci.openrio.toast.core.loader.simulation.jni;

import edu.wpi.first.wpilibj.hal.InterruptJNI;
import jaci.openrio.toast.core.ToastBootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A container object for what would be a DIO Interrupt. Because this is a simulation, we have to simulate the interrupt
 *
 * @author Jaci
 */
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

    /**
     * Set the DIO pin of the interrupt
     */
    public void setPin(int p) {
        this.pin = p;
    }

    /**
     * @return The DIO pin of the interrupt
     */
    public int getPin() {
        return pin;
    }

    /**
     * Get an InterruptContainer for the given DIO Port. Return null if not
     * registered
     */
    public static InterruptContainer getByPin(int p) {
        for (InterruptContainer cont : interrupts) {
            if (cont.pin == p) return cont;
        }
        return null;
    }

    /**
     * Blocks the current thread until an interrupt is detected. Try not to do this in the main thread, as it will
     * cause the Thread to be blocked until the timeout period is over
     */
    public synchronized void waitForInterrupt(double timeout) {
        try {
            wait((long) (timeout * 1000));
        } catch (Exception e) {
        }
    }

    /**
     * Enable the Interrupt
     */
    public void setEnabled(boolean s) {
        this.enabled = s;
    }

    /**
     * Get the container for the given Pointer ID
     */
    public static InterruptContainer getByIndex(int index) {
        return interrupts.get(index);
    }

    /**
     * Setup which phase the interrupt triggers on
     */
    public void setup(boolean triggerRising, boolean triggerFalling) {
        this.triggerRising = triggerRising;
        this.triggerFalling = triggerFalling;
    }

    /**
     * Set the Interrupt callback
     */
    public void setFunc(InterruptJNI.InterruptJNIHandlerFunction func, Object param) {
        this.func = func;
        this.funcParam = param;
    }

    /**
     * Trigger the interrupt
     */
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

    /**
     * Get the Timestamp of the trigger
     */
    public long getTime(boolean isRising) {
        long t = isRising ? risingTime : fallingTime;
        t -= ToastBootstrap.startTimeMS;
        return t / 1000;
    }

}
