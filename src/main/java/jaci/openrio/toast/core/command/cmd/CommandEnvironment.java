package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Environment;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.IHelpable;
import jaci.openrio.toast.lib.util.ToastUtil;

/**
 * Prints out a bunch of details about the Robotics Environment
 *
 * @author Jaci
 */
public class CommandEnvironment extends AbstractCommand implements IHelpable {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "environment";
    }

    /**
     * Returns the 'alias' for the command (other names)
     */
    public String[] getAlias() {
        return new String[] {"env"};
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
        boolean raw = false;
        if (ToastUtil.contains(args, "-raw"))
            raw = true;
        for (String line : Environment.toLines())
            if (raw) System.out.println(line);
            else Toast.log().info(line);
    }

    /**
     * Returns a help message to display with the 'help' command
     */
    @Override
    public String getHelp() {
        return "Prints details about the Robot Environment";
    }
}
