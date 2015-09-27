package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.CommandBus;
import jaci.openrio.toast.core.script.js.JavaScript;

/**
 * The JavaScript command will open an interactive JavaScript prompt for live-scripting, similar to the 'jjs' command
 * on systems with Oracle Nashorn installed.
 *
 * @author Jaci
 */
public class CommandJScript extends AbstractCommand {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "js";
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
        boolean run = true;
        Toast.log().info("Interactive Toast JavaScript console.");
        while (run) {
            try {
                String message = CommandBus.requestNextMessage();
                if (message == null) {
                    run = false;
                    continue;
                }
                if (message.equals("exit") || message.equals("stop")) {
                    run = false;
                    continue;
                }
                Object ret = JavaScript.eval(message);
                if (ret != null)
                    Toast.log().raw("#=> " + ret.toString());
                else
                    Toast.log().raw("#=> null");
            } catch (InterruptedException e) {
                run = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.log().info("Interactive JavaScript console stopped.");
    }
}
