package jaci.openrio.toast.core.shared;

import java.util.Vector;

/**
 * The Module Event Bus is a bus that allows for Modules to raise events
 * globally. This Event Bus allows for Modules to be optional dependencies, but
 * still have methods invoked/listened for
 *
 * This can also be used for communication inside of your own Module. Kinda like how you
 * can use a bag or suitcase as a foot-stool. It's not a bug, it's a feature.
 *
 * @author Jaci
 */
public class ModuleEventBus {

    static volatile Vector<ModuleEventListener> listeners = new Vector<>();

    /**
     * Register a {@link jaci.openrio.toast.core.shared.ModuleEventListener} to the
     * bus. This is required for a class to be able to listen for events
     */
    public static void registerListener(ModuleEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Raise an event to all listeners on the bus
     * @param sender The sender of the event. This is usually your Module's name unless otherwise
     *               defined
     * @param event_type The type of event to raise. This should be a unique identifier
     * @param data The data passed to the event, or null if none are provided
     */
    public static void raiseEvent(String sender, String event_type, Object... data) {
        for (ModuleEventListener listener : listeners)
            listener.onModuleEvent(sender, event_type, data);
    }

    /**
     * Raise an event to all listeners on the bus, in a new Thread
     * @param sender The sender of the event. This is usually your Module's name unless otherwise
     *               defined
     * @param event_type The type of event to raise. This should be a unique identifier
     * @param data The data passed to the event, or null if none are provided
     */
    public static void raiseConcurrentEvent(String sender, String event_type, Object... data) {
        new Thread() {
            public void run() {
                for (ModuleEventListener listener : listeners)
                    listener.onModuleEvent(sender, event_type, data);
            }
        }.start();
    }

}
