package toast.examples;

import jaci.openrio.delegate.BoundDelegate;
import jaci.openrio.delegate.Security;
import jaci.openrio.toast.core.network.SocketManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class EchoDelegate implements BoundDelegate.ConnectionCallback {

    BoundDelegate delegate_instance;

    public void register() {            // This would be called from your Main module class
        delegate_instance = SocketManager.register("MyEchoDelegate");
        delegate_instance.callback(this);
        delegate_instance.setPassword("hunter2", Security.HashType.SHA1);
    }

    @Override
    public void onClientConnect(Socket clientSocket, BoundDelegate delegate) {          // This is called when a client is connected, has entered the correct password (if one exists) and is on the right port
        Thread echoThread = new Thread(new Runnable() {
            @Override
            public void run() {                     // Just echos what the client said
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                    while (true) {
                        String line = reader.readLine();
                        output.writeBytes(line);
                    }
                } catch (IOException e) { }
            }
        });
    }
}
