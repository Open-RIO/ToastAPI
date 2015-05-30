package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;

/**
 * Simply exits Toast and stops the Simulation GUI
 *
 * @author Jaci
 */
public class CommandExit extends AbstractCommand {
    @Override
    public String getCommandName() {
        return "exit";
    }

    public String[] getAlias() {
        return new String[] {"quit", ":q"};
    }

    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        Toast.getToast().shutdownSafely();
    }
}
