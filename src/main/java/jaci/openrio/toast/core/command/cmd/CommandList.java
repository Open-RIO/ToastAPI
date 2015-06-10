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
    @Override
    public String getCommandName() {
        return "list";
    }

    @Override
    public String getHelp() {
        return "Will list all the commands registered on the Command Bus";
    }

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
