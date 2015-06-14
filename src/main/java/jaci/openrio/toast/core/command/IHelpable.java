package jaci.openrio.toast.core.command;

/**
 * Provider for the 'Help' command. Commands implementing this interface provide a 'help' message
 * to allow the user to know what exactly the command does, as well as it's arguments.
 *
 * @author Jaci
 */
public interface IHelpable {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    public String getCommandName();

    /**
     * Returns a help message to display with the 'help' command
     */
    public String getHelp();

}
