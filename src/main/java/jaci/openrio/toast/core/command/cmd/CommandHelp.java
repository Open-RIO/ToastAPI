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
    @Override
    public String getCommandName() {
        return "help";
    }

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

    public void help(IHelpable helpable) {
        Toast.log().info(helpable.getCommandName() + " -- " + helpable.getHelp());
    }
}
