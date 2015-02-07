package jaci.openrio.toast.core.webui;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The registry for the Web UI. This is where {@link jaci.openrio.toast.core.webui.WebHandler} is registered
 * and where variables are set. Variables in the webui are signified by ${var_name} in the web pages and are
 * set in setVar() using var_name without the ${}
 *
 * Thread Dispatching for the webui is also handled here.
 *
 * @author Jaci
 */
public class WebRegistry {

    /**
     * The package tree where the WebUI assets are located
     */
    public static final String ASSETS_ROOT = "toast/web/html";

    protected static ArrayList<WebHandler> handlers;

    protected static HashMap<String, String> variables;

    /**
     * Initialize the registry. This is handled by Toast
     */
    public static void init() {
        handlers = new ArrayList<WebHandler>();
        variables = new HashMap<String, String>();
        startDispatch();
    }

    /**
     * Register a {@link jaci.openrio.toast.core.webui.WebHandler} into the registry
     */
    public static void addHandler(WebHandler handler) {
        handlers.add(handler);
    }

    /**
     * Set a variable for the WebUI to replace
     */
    public static void setVar(String id, String val) {
        variables.put(id, val);
    }

    /**
     * Start the WebUI. This is handled by Toast.
     */
    public static void startDispatch() {
        Thread threadDispatch = new Thread() {
            public void run() {
                setName("Web UI Dispatcher");
                try {
                    ServerSocket socket = new ServerSocket(5808);

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
