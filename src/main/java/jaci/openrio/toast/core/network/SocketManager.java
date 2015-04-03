package jaci.openrio.toast.core.network;

import jaci.openrio.delegate.BoundDelegate;
import jaci.openrio.delegate.DelegateServer;

/**
 * The class responsible for managing Socket communication between Toast Modules and other programs running
 * on the Driver Station. This class implements the NetworkDelegate API in order to make sure modules do
 * not conflict on sockets, as well as making sure sockets are within the limits FMS provides, having only
 * 10 sockets available, specifically, 5800-5810. This SocketManager listens on ports 5806-5810, having the
 * master socket on port 5805. It is recommended to look though the NetworkDelegate API Documentation for
 * more information regarding the master-slave socket lifecycle. Ports 5800-5804 are left open for communication
 * that requires a specific protocol, such as the WebUI that needs a HTTP communication. Unless you absolutely
 * require a specific socket, it is highly recommended you use this class.
 *
 * @author Jaci
 */
public class SocketManager {

    static DelegateServer delegateServer;

    static boolean launch = false;

    /**
     * Initiate the Delegate Server, but do not Launch it. Leave this up to Toast to handle.
     */
    public static void init() {
        delegateServer = DelegateServer.createRange(5805, 5806, 5810);
    }

    /**
     * Register your own Delegate. The String 'id' parameter should be your module name, prepended
     * by your package name. This is to make sure there are no conflicts. e.g. 'com.yourname.YourModule'
     */
    public static BoundDelegate register(String id) {
        launch = true;
        return delegateServer.requestDelegate(id);
    }

    /**
     * Launch the DelegateServer. This should be left to Toast to handle.
     */
    public static void launch() {
        if (launch) {
            new Thread() {
                public void run() {
                    try {
                        delegateServer.launchDelegate();
                    } catch (Exception e) { }
                }
            }.start();
        }
    }

}
