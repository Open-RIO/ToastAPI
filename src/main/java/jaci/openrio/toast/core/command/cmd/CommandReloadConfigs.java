package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.IHelpable;
import jaci.openrio.toast.lib.module.ModuleConfig;

/**
 * The Command to reload all configuration files. This will re-read all the configs from file.
 *
 * @author Jaci
 */
public class CommandReloadConfigs extends AbstractCommand implements IHelpable {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "reloadcfg";
    }

    /**
     * Returns a help message to display with the 'help' command
     */
    @Override
    public String getHelp() {
        return "Reload all ModuleConfig files";
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
        int cnt = 0;
        for (ModuleConfig config : ModuleConfig.allConfigs) {
            config.reload();
            cnt++;
        }
        Toast.log().info("All ModuleConfigs reloaded (" + cnt + " reloaded)");
    }
}
