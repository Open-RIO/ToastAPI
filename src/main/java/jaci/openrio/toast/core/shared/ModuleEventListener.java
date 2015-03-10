package jaci.openrio.toast.core.shared;

/**
 * Implement this on classes you wish to act as listeners on the {@link jaci.openrio.toast.core.shared.ModuleEventBus}
 *
 * These classes are simple listeners for the events broadcast to all modules. This allows for optional dependencies
 * to trigger/listen for method invocations by other modules or Toast itself
 *
 * @author Jaci
 */
public interface ModuleEventListener {

    /**
     * Invoked when an event is broadcast to this listener
     * @param sender The sender of the event. This is usually the Name of a Module
     * @param event_type The type of event that was raised. These are unique. See module
     *                   documentation for more details
     * @param data The data passed with the event. Keep in mind this can be null if no data
     *             was provided. See module documentation for more details
     */
    public void onModuleEvent(String sender, String event_type, Object... data);

}
