package jaci.openrio.toast.core.loader.simulation;

import com.sun.tools.javadoc.Start;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.lib.state.RobotState;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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

    public static void broadcast() {
        String service = "roborio-" + ToastConfiguration.Property.SIM_BROADCAST_TEAM + "-frc";
        String hostname = "roborio-" + ToastConfiguration.Property.SIM_BROADCAST_TEAM + "-frc";
        String ip_str = "127.0.0.1";

        char ip[] = new char[4];
        String arr[] = ip_str.split("\\.");
        for (int i = 0; i < 4; i++) {
            ip[i] = (char) (int) Integer.parseInt(arr[i]);
        }

        char payload_1[] = {
                0x00, 0x00, 0x84, 0x00,        // ID, Response Query
                0x00, 0x00, 0x00, 0x03,        // No Question, 3 Answers
                0x00, 0x00, 0x00, 0x01,        // No Authority, 1 Additional RR
        };

        char payload_2[] = {
                0x03,                            // Len: 3
                0x5f, 0x6e, 0x69,                // _ni
                0x04,                            // Len: 4
                0x5f, 0x74, 0x63, 0x70,            // _tcp
                0x05,                            // Len: 5
                0x6c, 0x6f, 0x63, 0x61, 0x6c,    // local
                0x00,                            // end of string

                0x00, 0x0c, 0x80, 0x01,            // Type: PTR (domain name PoinTeR), Class: IN, Cache flush: true
                0x00, 0x00, 0x00, 0x3C,            // TTL: 60 Sec
                0x00, (char) (0x03 + service.length()),
                (char) service.length()
        };

        char payload_3[] = service.toCharArray();

        char payload_4[] = {
                0xc0, 0x0c,                    // Name Offset (0xc0, 0x0c => 12 =>._ni._tcp.local)

                // Record 2: SRV
                0xc0, 0x26, 0x00, 0x21,        // Name Offset (mdns.service_name), Type: SRV (Server Selection)
                0x80, 0x01,                    // Class: IN, Cache flush: true
                0x00, 0x00, 0x00, 0x3C,        // TTL: 60 sec
                0x00, (char) (0xE + hostname.length()),    // Data Length: 14 + thnl
                0x00, 0x00, 0x00, 0x00,        // Priority: 0, Weight: 0
                0x0d, 0xfc,                    // Port: 3580
                (char) hostname.length()                    // Len: thnl
        };

        char payload_5[] = hostname.toCharArray();

        char payload_6[] = {
                0x05,                            // Len: 5
                0x6c, 0x6f, 0x63, 0x61, 0x6c,    // local
                0x00,                            // end of string

                // Record 3: TXT
                0xc0, 0x26, 0x00, 0x10,        // Name Offset (mdns.service_name), Type: TXT
                0x80, 0x01,                    // Class: IN, Cache flush: true
                0x00, 0x00, 0x00, 0x3C,        // TTL: 60 sec
                0x00, 0x01, 0x00,            // Data Length: 1, TXT Length: 0

                // Additional Record: A
                0xc0, (char) (0x3b + service.length()),    // Name Offset (mdns.target_host_name)
                0x00, 0x01, 0x80, 0x01,        // Type: A, Class: IN, Cache flush: true
                0x00, 0x00, 0x00, 0x3C,        // TTL: 60 sec
                0x00, 0x04,                    // Data Length: 4
                ip[0], ip[1], ip[2], ip[3]    // IP Bytes
        };

        char payload[] = new char[payload_1.length + payload_2.length + payload_3.length + payload_4.length + payload_5.length + payload_6.length];
        System.arraycopy(payload_1, 0, payload, 0, payload_1.length);
        int l = payload_1.length;
        System.arraycopy(payload_2, 0, payload, l, payload_2.length);
        l += payload_2.length;
        System.arraycopy(payload_3, 0, payload, l, payload_3.length);
        l += payload_3.length;
        System.arraycopy(payload_4, 0, payload, l, payload_4.length);
        l += payload_4.length;
        System.arraycopy(payload_5, 0, payload, l, payload_5.length);
        l += payload_5.length;
        System.arraycopy(payload_6, 0, payload, l, payload_6.length);

        try {
            InetAddress group = InetAddress.getByName("224.0.0.251");
            MulticastSocket multicast_socket = new MulticastSocket(5353);
            multicast_socket.joinGroup(group);

            byte[] byte_payload = new byte[payload.length];
            for(int i = 0; i < payload.length; i++) {
                byte_payload[i] = (byte) payload[i];
            }

            Toast.log().info("Driver Station Communications -> Broadcast Running!");
            while (true) {
                DatagramPacket packet = new DatagramPacket(byte_payload, byte_payload.length, group, 5353);
                multicast_socket.send(packet);
                Thread.sleep(5000);
            }
        } catch (Exception e) { }
    }

    public static void run() {
        if (ToastConfiguration.Property.SIM_BROADCAST_MDNS.asBoolean()) {
            Thread broadcastThread = new Thread(group, DriverStationCommunications::broadcast);
            broadcastThread.run();
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
