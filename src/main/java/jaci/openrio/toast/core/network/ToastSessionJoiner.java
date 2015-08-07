package jaci.openrio.toast.core.network;

import jaci.openrio.delegate.DelegateClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ToastSessionJoiner {

    static DelegateClient LOGGER_DELEGATE;
    static DelegateClient COMMAND_DELEGATE;

    static DataOutputStream commands_out;
    static BufferedReader logger_in;

    public static void init() {
        LOGGER_DELEGATE = new DelegateClient("localhost", 5805, "TOAST_logger");
        COMMAND_DELEGATE = new DelegateClient("localhost", 5805, "TOAST_command");

        try {
            LOGGER_DELEGATE.connect();
            COMMAND_DELEGATE.connect();

            commands_out = new DataOutputStream(COMMAND_DELEGATE.getSocket().getOutputStream());
            logger_in = new BufferedReader(new InputStreamReader(LOGGER_DELEGATE.getSocket().getInputStream()));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    try {
                        while ((line = logger_in.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        System.err.println("Unexpected error while reading from Logger Delegate");
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if (line.trim().equalsIgnoreCase("exit"))
                    System.exit(0);
                commands_out.writeBytes(line + "\n");
            }
        } catch (Exception e) {
            System.err.println("Something went wrong while establishing a connection to the Toast instance...");
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
