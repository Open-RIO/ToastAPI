package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.IHelpable;
import jaci.openrio.toast.lib.module.ModuleConfig;

public class CommandReloadConfigs extends AbstractCommand implements IHelpable {

    @Override
    public String getCommandName() {
        return "reloadcfg";
    }

    @Override
    public String getHelp() {
        return "Reload all ModuleConfig files";
    }

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
