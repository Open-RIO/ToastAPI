package edu.wpi.first.wpilibj.hal;

import jaci.openrio.toast.core.loader.simulation.jni.InterruptContainer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class InterruptJNI extends JNIWrapper {

    public interface InterruptJNIHandlerFunction {
        void apply(int interruptAssertedMask, Object param);
    }

    ;

    private static InterruptContainer get(ByteBuffer point) {
        return InterruptContainer.getByIndex(point.get(0));
    }

    public static void initializeInterruptJVM(IntBuffer status) {
    }

    public static ByteBuffer initializeInterrupts(int interruptIndex, byte watcher, IntBuffer status) {
        InterruptContainer container = new InterruptContainer();
        return ByteBuffer.allocate(1).put((byte) InterruptContainer.interrupts.indexOf(container));
    }

    public static void cleanInterrupts(ByteBuffer interrupt_pointer, IntBuffer status) {
        byte index = interrupt_pointer.get(0);
        InterruptContainer.interrupts.remove(get(interrupt_pointer));
    }

    public static int waitForInterrupt(ByteBuffer interrupt_pointer, double timeout, boolean ignorePrevious, IntBuffer status) {
        get(interrupt_pointer).waitForInterrupt(timeout);
        return 0;
    }

    public static void enableInterrupts(ByteBuffer interrupt_pointer, IntBuffer status) {
        get(interrupt_pointer).setEnabled(true);
    }

    public static void disableInterrupts(ByteBuffer interrupt_pointer, IntBuffer status) {
        get(interrupt_pointer).setEnabled(false);
    }

    public static double readRisingTimestamp(ByteBuffer interrupt_pointer, IntBuffer status) {
        return get(interrupt_pointer).getTime(true);
    }

    public static double readFallingTimestamp(ByteBuffer interrupt_pointer, IntBuffer status) {
        return get(interrupt_pointer).getTime(false);
    }

    public static void requestInterrupts(ByteBuffer interrupt_pointer, byte routing_module, int routing_pin, byte routing_analog_trigger, IntBuffer status) {
        get(interrupt_pointer).setPin(routing_pin);
    }

    public static void attachInterruptHandler(ByteBuffer interrupt_pointer, InterruptJNIHandlerFunction handler, Object param, IntBuffer status) {
        get(interrupt_pointer).setFunc(handler, param);
    }

    public static void setInterruptUpSourceEdge(ByteBuffer interrupt_pointer, byte risingEdge, byte fallingEdge, IntBuffer status) {
        get(interrupt_pointer).setup(risingEdge != 0, fallingEdge != 0);
    }

}
