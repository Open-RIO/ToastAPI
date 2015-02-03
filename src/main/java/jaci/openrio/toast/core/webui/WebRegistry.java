package jaci.openrio.toast.core.webui;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class WebRegistry {

    public static final String ASSETS_ROOT = "toast/web/html";

    protected static ArrayList<WebHandler> handlers;

    protected static HashMap<String, String> variables;

    public static void init() {
        handlers = new ArrayList<WebHandler>();
        variables = new HashMap<String, String>();
        startDispatch();
    }

    public static void addHandler(WebHandler handler) {
        handlers.add(handler);
    }

    public static void setVar(String id, String val) {
        variables.put(id, val);
    }

    public static void startDispatch() {
        Thread threadDispatch = new Thread() {
            public void run() {
                setName("Web UI Dispatcher");
                try {
                    ServerSocket socket = new ServerSocket(1337);

                    while (true) {
                        Socket clientSocket = socket.accept();

                        new ThreadWebClient(clientSocket);
                    }
                } catch (Exception e) { }
            }
        };
        threadDispatch.start();
    }

}
