package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.CommandBus;
import jaci.openrio.toast.core.script.js.JavaScript;

import javax.script.ScriptException;

public class CommandJScript extends AbstractCommand {

    @Override
    public String getCommandName() {
        return "js";
    }

    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        boolean run = true;
        Toast.log().info("Interactive Toast JavaScript console.");
        while (run) {
            try {
                String message = CommandBus.requestNextMessage();
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
            } catch (ScriptException e) {
                Toast.log().raw(e.toString());
            }
        }
        Toast.log().info("Interactive JavaScript console stopped.");
    }
}
