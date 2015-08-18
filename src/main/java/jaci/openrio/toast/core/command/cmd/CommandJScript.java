package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.CommandBus;
import jaci.openrio.toast.core.script.js.JavaScript;

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
