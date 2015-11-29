package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.lib.state.RobotState;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Simulated DriverStation communication library
 *
 * @author Jaci
 */
public class DriverStationCommunications {

    public static ThreadGroup group = new ThreadGroup("driver_station");

    public static void init() {
        if (ToastConfiguration.Property.SIM_DS_ENABLED.asBoolean()) {
            Thread runThread = new Thread(group, DriverStationCommunications::run);
            runThread.start();
        }
    }

    public static void run() {
        if (ToastConfiguration.Property.SIM_BONJOUR_ENABLED.asBoolean()) {
            String bonjour_target = ToastConfiguration.Property.SIM_BONJOUR_TARGET.asString();
            try {
                ProcessBuilder pb = new ProcessBuilder("dns-sd", "-P", "roboRIO-TOAST", "_ni._tcp", "local", "3580", "roboRIO-TOAST.local", "127.0.0.1");
                Process p = pb.start();
                ProcessBuilder pb2 = new ProcessBuilder("dns-sd", "-P", "roboRIO-" + bonjour_target, "_ni._tcp", "local", "3580", "roboRIO-" + bonjour_target + ".local", "127.0.0.1");
                Process p2 = pb2.start();
            } catch (Exception e) {
                Toast.log().error("Could not start Toast DriverStation Bonjour Service (do you have Bonjour installed?): " + e);
            }
        }

        try {
            DatagramSocket socket = new DatagramSocket(1110);
            byte[] buffer = new byte[8192];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true) {
                socket.receive(packet);
                decodePacket(packet, buffer);
                byte[] toSend = encodePacket();
                DatagramPacket sendPacket = new DatagramPacket(toSend, toSend.length, packet.getAddress(), 1150);
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            Toast.log().error("Could not start Toast DriverStation Networking Service: " + e);
            Toast.log().exception(e);
        }
    }

    static byte pi = 0;
    static byte ng = 0;
    static byte control = 0;
    public static boolean connected = false;

    public static short[][] joyaxis = new short[6][12];
    public static int[] joybuttons = new int[6];
    public static byte[] joybuttoncount = new byte[6];
    public static short[][] joypov = new short[6][1];

    public static void decodePacket(DatagramPacket packet, byte[] buffer) {
        connected = true;
        pi = buffer[0]; ng = buffer[1];
        if (buffer[2] != 0) {
            // General Packet
            byte ctrl = buffer[3];
            control = ctrl;
            SimulationData.currentState = ctrl == 0 ? RobotState.DISABLED :
                    ctrl == 4 ? RobotState.TELEOP :
                    ctrl == 6 ? RobotState.AUTONOMOUS :
                    ctrl == 5 ? RobotState.TEST :
                    RobotState.DISABLED;
            SimulationData.repaintState();

            SimulationData.alliance_station = buffer[5];

            int i = 6;
            boolean search = true;
            int joyid = 0;

            // Joysticks
            while (i < buffer.length && search) {
                int structure_size = buffer[i];
                search = buffer[i + 1] == 0x0c;
                if (!search) continue;

                int axis_count = buffer[i + 2];
                joyaxis[joyid] = new short[axis_count];
                for (int ax = 0; ax < axis_count; ax++) {
                    int ax_val = buffer[i + 2 + ax + 1];
                    joyaxis[joyid][ax] = (short) ax_val;
                }
                int b = i + 2 + axis_count + 1;

                int button_count = buffer[b];
                joybuttoncount[joyid] = (byte) button_count;

                int button_delta = (button_count / 8 + ((button_count%8 == 0) ? 0 : 1));
                int total_mask = 0;
                for (int bm = 0; bm < button_delta; bm++) {
                    byte button_mask = buffer[b + bm + 1];
                    total_mask = (total_mask << (bm * 8)) | button_mask;
                }
                joybuttons[joyid] = total_mask;

                b = b + button_delta + 1;

                int pov_count = buffer[b];
                joypov[joyid] = new short[pov_count];
                for (int pv = 0; pv < pov_count; pv++) {
                    int a1 = buffer[b + 1 + (pv*2)];
                    int a2 = buffer[b + 1 + (pv*2) + 1];
                    if (a2 < 0) a2 = 256 + a2;
                    short result = (short) (a1 << 8 | a2);
                    joypov[joyid][pv] = result;
                }

                joyid++;
                i += structure_size + 1;
            }
        } else {
            // Connection Packet
        }
    }

    public static byte[] encodePacket() {
        byte[] buffer = new byte[8];
        buffer[0] = pi; buffer[1] = ng;
        buffer[2] = 0x01;
        buffer[3] = control;
        buffer[4] = 0x10 | 0x20;

        double volts = SimulationData.pdpVoltage;
        // Splits Bat Voltage into two bytes
        buffer[5] = (byte)volts;
        buffer[6] = (byte)((volts * 100 - ((byte)volts) * 100) * 2.5);
        buffer[7] = 0;
        return buffer;
    }

}
