package edu.wpi.first.wpilibj.hal;

import java.nio.ByteBuffer;

public class JNIWrapper {

    public static ByteBuffer getPort(byte pin) {
        return ByteBuffer.allocateDirect(1).put(pin);
    }

}
