package jaci.openrio.toast.core.network;

import jaci.openrio.delegate.BoundDelegate;
import jaci.openrio.delegate.Security;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.lib.log.ColorPrint;
import jaci.openrio.toast.lib.log.LogHandler;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.log.SysLogProxy;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

/**
 * A one-way delegate for the Command Line. This Delegate will only send messages that are a direct output from the Logger
 * No data will be received from the client. Commands should be interpreted through the {@link jaci.openrio.toast.core.network.CommandDelegate}
 *
 * DelegateID: "TOAST_logger"
 *
 * @author Jaci
 */
public class LoggerDelegate extends OutputStream implements BoundDelegate.ConnectionCallback, ColorPrint.ColorStream {

    static BoundDelegate server;
    static Vector<Client> clients;

    /**
     * Initialize the Delegate. This registers the delegate on the SocketManager, as well as configures it if a
     * password or hash-type is provided in the Toast Configuration files. This is already called by Toast, so it is
     * not necessary to call this method yourself.
     */
    public static void init() {
        server = SocketManager.register("TOAST_logger");
        String pass = ToastConfiguration.Property.LOGGER_DELEGATE_PASSWORD.asString();
        String algorithm = ToastConfiguration.Property.LOGGER_DELEGATE_ALGORITHM.asString();
        if (pass != null && !pass.equals("")) {
            if (algorithm != null && Security.HashType.match(algorithm) != null)
                server.setPassword(pass, Security.HashType.match(algorithm));
            else
                server.setPassword(pass);
        }
        clients = new Vector<>();
        LoggerDelegate instance = new LoggerDelegate();
        server.callback(instance);
        SysLogProxy.master.add(instance);
        SysLogProxy.masterError.add(instance);
    }

    /**
     * Broadcast a message to all clients
     */
    public static void broadcast(String message) {
        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client client = it.next();
            try {
                if (!client.client.isClosed()) {
                    client.output.writeBytes(message + "\n");
                    continue;
                }
            } catch (Exception e) {}
            it.remove();
        }
    }

    /**
     * Called when a Client is Connected to the Socket. This is used to register a client to broadcast to
     * @param clientSocket The socket of the Client
     * @param delegate The Delegate this callback is triggered on.
     */
    @Override
    public void onClientConnect(Socket clientSocket, BoundDelegate delegate) {
        new Thread() {
            public void run() {
                this.setName("LoggerBacklog");
                try {
                    BufferedReader file_reader = new BufferedReader(new FileReader(SysLogProxy.recentOut));
                    DataOutputStream socket_out = new DataOutputStream(clientSocket.getOutputStream());
                    String ln;
                    socket_out.writeBytes("***** BEGIN BACKLOG *****\n");
                    while ((ln = file_reader.readLine()) != null)
                        socket_out.writeBytes(ln + "\n");
                    socket_out.writeBytes("***** END BACKLOG *****\n");
                    file_reader.close();

                    Client client = new Client();
                    client.client = clientSocket;
                    client.output = socket_out;
                    clients.add(client);
                } catch (java.io.IOException e) {
                    Toast.log().error("Could not connect Logger Client: ");
                    Toast.log().exception(e);
                }
            }
        }.start();
    }

    @Override
    public void write(int b) throws IOException {
        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client client = it.next();
            try {
                client.output.write(b);
            } catch (Exception e) {
                it.remove();
            }
        }
    }

    @Override
    public void write(byte b[]) throws IOException {
        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client client = it.next();
            try {
                client.output.write(b);
            } catch (Exception e) {
                it.remove();
            }
        }
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client client = it.next();
            try {
                client.output.write(b, off, len);
            } catch (Exception e) {
                it.remove();
            }
        }
    }

    @Override
    public void flush() throws IOException {
        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client client = it.next();
            try {
                client.output.flush();
            } catch (Exception e) {
                it.remove();
            }
        }
    }

    public static class Client {

        public DataOutputStream output;
        public Socket client;

    }
}
