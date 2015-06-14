package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.CommandBus;
import jaci.openrio.toast.core.command.FuzzyCommand;
import jaci.openrio.toast.core.command.IHelpable;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a list of all Abstract Commands in the CommandBus.
 *
 * @author Jaci
 */
public class CommandList extends AbstractCommand implements IHelpable {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "list";
    }

    /**
     * Returns a help message to display with the 'help' command
     */
    @Override
    public String getHelp() {
        return "Will list all the commands registered on the Command Bus";
    }

    /**
     * Invoke the command if the name matches the one to be triggered
     * @param argLength The amount of arguments in the 'args' param
     * @param args The arguments the command was invoked with. This can be empty if
     *             none were provided. Keep in mind this does NOT include the Command Name.
     *             Args are separated by spaces
     * @param command The full command message
     */
    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        List<String> strList = new ArrayList<>();
        for (AbstractCommand cmd : CommandBus.commands) {
            strList.add(cmd.getCommandName());
        }
        for (FuzzyCommand cmd : CommandBus.parsers) {
            if (cmd instanceof IHelpable)
                strList.add(((IHelpable) cmd).getCommandName());
        }
        String join = String.join(", ", strList);
        Toast.log().info("Commands List: " + join);
    }
}
