package jaci.openrio.toast.core.command;

/**
 * The base, abstract class for Commands to be registered on the {@link jaci.openrio.toast.core.command.CommandBus}
 *
 * Commands are processed and invoked if they match. This allows for actions to be triggered on the Robot remotely
 * or locally
 *
 * @author Jaci
 */
public abstract class AbstractCommand {

    /**
     * Get the command name
     *
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    public abstract String getCommandName();

    /**
     * Invoke the command if the name matches the one to be triggered
     * @param argLength The amount of arguments in the 'args' param
     * @param args The arguments the command was invoked with. This can be empty if
     *             none were provided. Keep in mind this does NOT include the Command Name.
     *             Args are separated by spaces
     * @param command The full command message
     */
    public abstract void invokeCommand(int argLength, String[] args, String command);

}
