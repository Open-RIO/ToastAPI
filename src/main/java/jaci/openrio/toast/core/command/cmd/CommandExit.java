package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;

/**
 * Simply exits Toast and stops the Simulation GUI
 *
 * @author Jaci
 */
public class CommandExit extends AbstractCommand {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "exit";
    }

    /**
     * Returns the 'alias' for the command (other names)
     */
    public String[] getAlias() {
        return new String[] {"quit", ":q"};
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
        Toast.getToast().shutdownSafely();
    }
}
