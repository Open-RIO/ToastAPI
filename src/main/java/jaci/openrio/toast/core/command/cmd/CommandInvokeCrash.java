package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.IHelpable;
import jaci.openrio.toast.lib.crash.CrashHandler;

/**
 * Invokes a Debug Crash. Useful for testing things like custom Crash Handlers
 *
 * @author Jaci
 */
public class CommandInvokeCrash extends AbstractCommand implements IHelpable {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "crash";
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
        Toast.log().error("Goodbye cruel world!");
        CrashHandler.handle(new Exception("Invoked Debug Crash"));
    }

    /**
     * Returns a help message to display with the 'help' command
     */
    @Override
    public String getHelp() {
        return "Invokes a debugging crash";
    }
}
