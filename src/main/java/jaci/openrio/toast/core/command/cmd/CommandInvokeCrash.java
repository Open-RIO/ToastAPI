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
    @Override
    public String getCommandName() {
        return "crash";
    }

    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        Toast.log().error("Goodbye cruel world!");
        CrashHandler.handle(new Exception("Invoked Debug Crash"));
    }

    @Override
    public String getHelp() {
        return "Invokes a debugging crash";
    }
}
