package jaci.openrio.toast.core.command;

/**
 * The base, abstract class for commands registered on the {@link jaci.openrio.toast.core.command.CommandBus}, but
 * do not follow the standard command type. For example, FuzzyCommands may search for a string in a message instead
 * of checking if it begins with the command name.
 *
 * @author Jaci
 */
public abstract class FuzzyCommand {

    /**
     * Should this Command be invoked with the given message?
     */
    public abstract boolean shouldInvoke(String message);

    /**
     * Invokes the command if {@link #shouldInvoke} returns true.
     * @param message The full command message. This is left un-parsed so you can handle
     *                it yourself
     */
    public abstract void invokeCommand(String message);

}
