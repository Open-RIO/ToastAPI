package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.jni.InterruptContainer;

public class InterruptJNI extends JNIWrapper {

    public interface InterruptJNIHandlerFunction {
        void apply(int interruptAssertedMask, Object param);
    }

    private static InterruptContainer get(int point) {
        return InterruptContainer.getByIndex(point);
    }

    public static int initializeInterrupts(int interruptIndex, byte watcher) {
        InterruptContainer container = new InterruptContainer();
        return getPort((byte) InterruptContainer.interrupts.indexOf(container));
    }

    public static void cleanInterrupts(int interrupt_pointer) {
        InterruptContainer.interrupts.remove(get(interrupt_pointer));
    }

    public static int waitForInterrupt(int interrupt_pointer, double timeout, boolean ignorePrevious) {
        get(interrupt_pointer).waitForInterrupt(timeout);
        return 0;
    }

    public static void enableInterrupts(int interrupt_pointer) {
        get(interrupt_pointer).setEnabled(true);
    }

    public static void disableInterrupts(int interrupt_pointer) {
        get(interrupt_pointer).setEnabled(false);
    }

    public static double readRisingTimestamp(int interrupt_pointer) {
        return get(interrupt_pointer).getTime(true);
    }

    public static double readFallingTimestamp(int interrupt_pointer) {
        return get(interrupt_pointer).getTime(false);
    }

    public static void requestInterrupts(int interrupt_pointer, byte routing_module, int routing_pin, byte routing_analog_trigger) {
        get(interrupt_pointer).setPin(routing_pin);
    }

    public static void attachInterruptHandler(int interrupt_pointer, InterruptJNIHandlerFunction handler, Object param) {
        get(interrupt_pointer).setFunc(handler, param);
    }

    public static void setInterruptUpSourceEdge(int interrupt_pointer, byte risingEdge, byte fallingEdge) {
        get(interrupt_pointer).setup(risingEdge != 0, fallingEdge != 0);
    }

}
