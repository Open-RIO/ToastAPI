package toast.examples;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.CommandBus;

import java.util.Arrays;

public class CommandDemo extends AbstractCommand {

    public void register() {                            // This would be called by your Main module class
        CommandBus.registerCommand(new CommandDemo());
    }

    @Override
    public String getCommandName() {
        return "demo";
    }

    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        Toast.log().info("Hello world! I was told to: " + Arrays.toString(args));
    }
}
