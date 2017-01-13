package edu.wpi.first.wpilibj.hal;

public class PortsJNI extends JNIWrapper {
    public static int getNumAccumulators() { return 0; }   // Unused

    public static int getNumAnalogTriggers() { return 0; }    // Unused

    public static int getNumAnalogInputs() { return 8; }        // 4 Onboard, 4 MXP

    public static int getNumAnalogOutputs() { return 2; }       // 2 MXP

    public static int getNumCounters() { return 0; }     // Unused

    public static int getNumDigitalHeaders() { return 0; }       // Unused

    public static int getNumPWMHeaders() { return 0; }       // Unused

    public static int getNumDigitalChannels() { return 26; }    // 10 Onboard, 16 MXP

    public static int getNumPWMChannels() { return 20; }    // 10 Onboard, 10 MXP

    public static int getNumDigitalPWMOutputs() { return 0; }   // Unused

    public static int getNumEncoders() { return 0; }        // Unused

    public static int getNumInterrupts() { return 0; }      // Unused

    public static int getNumRelayChannels() { return 0; }       // Unused

    public static int getNumRelayHeaders() { return 4; }    // 4 Onboard

    public static int getNumPCMModules() { return 1; }      // 1 (??) PCM

    public static int getNumSolenoidChannels() { return 8; }    // 8 Solenoids for PCM

    public static int getNumPDPModules() { return 1; }  // 1 PDP

    public static int getNumPDPChannels() { return 16; }    // 16 Motor Channels
}
