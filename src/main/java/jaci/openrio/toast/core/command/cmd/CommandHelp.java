package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.*;

import java.util.regex.Pattern;

/**
 * Provides a command for testing the 'help' of other commands. This is used to display help messages of commands
 * to the console so the user knows how to use the commands required.
 *
 * @author Jaci
 */
public class CommandHelp extends AbstractCommand {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "help";
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
        Pattern regex = null;
        if (args.length == 1) {
            regex = Pattern.compile(args[0]);
        } else if (args.length != 0) {
            throw new UsageException("help [command]");
        }

        for (AbstractCommand cmd : CommandBus.commands)
            tryRegex(regex, cmd);
        for (FuzzyCommand cmd : CommandBus.parsers)
            tryRegex(regex, cmd);
    }

    /**
     * Tests if the pattern provided with the Help Command matches the given Command object's name.
     * The Command Object must implement 'IHelpable' to be a valid command.
     */
    public void tryRegex(Pattern pattern, Object obj) {
        if (obj instanceof IHelpable) {
            if (pattern == null) {
                help((IHelpable) obj);
            } else {
                if (pattern.matcher(((IHelpable) obj).getCommandName()).matches())
                    help((IHelpable) obj);
            }
        }
    }

    /**
     * Returns a help message to display with the 'help' command
     */
    public void help(IHelpable helpable) {
        Toast.log().info(helpable.getCommandName() + " -- " + helpable.getHelp());
    }
}
