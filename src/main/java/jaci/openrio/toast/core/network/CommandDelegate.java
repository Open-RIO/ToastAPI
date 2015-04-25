package jaci.openrio.toast.core.network;

import jaci.openrio.delegate.BoundDelegate;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.CommandBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * A one-way delegate for the Command Line. This Delegate will only receive messages and will interpret them as commands.
 * No data will be sent to the client from this delegate. To read the output log, it's recommended to use the {@link jaci.openrio.toast.core.network.LoggerDelegate}
 *
 * DelegateID: "TOAST_command"
 *
 * @author Jaci
 */
public class CommandDelegate implements BoundDelegate.ConnectionCallback {

    static BoundDelegate server;

    public static void init() {
        server = SocketManager.register("TOAST_command");
        CommandDelegate instance = new CommandDelegate();
        server.callback(instance);
    }

    @Override
    public void onClientConnect(Socket clientSocket, BoundDelegate delegate) {
        new Thread() {
            public void run() {
                this.setName("CommandDelegate");
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    while (true)
                        CommandBus.parseMessage(reader.readLine());
                } catch (IOException e) {
                    Toast.log().error("Could not read Command Client: ");
                    Toast.log().exception(e);
                }
            }
        }.start();
    }

}
