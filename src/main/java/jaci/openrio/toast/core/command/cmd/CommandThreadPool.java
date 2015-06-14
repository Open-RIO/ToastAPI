package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.IHelpable;
import jaci.openrio.toast.core.thread.ToastThreadPool;
import jaci.openrio.toast.lib.log.Logger;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * This command will simply echo data about the {@link jaci.openrio.toast.core.thread.ToastThreadPool} to the console.
 * This is for debugging purposes
 *
 * command_name: 'threads'
 * args: nil
 *
 * @author Jaci
 */
public class CommandThreadPool extends AbstractCommand implements IHelpable {

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "threads";
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
        ThreadPoolExecutor e = (ThreadPoolExecutor) ToastThreadPool.INSTANCE.getService();
        Logger l = Toast.log();
        l.info("*Toast Thread Pool Instance Data: ");
        l.info(String.format("\t Active Threads: %d, Core Threads: %d", e.getPoolSize(), e.getCorePoolSize()));
        l.info(String.format("\t Active Jobs: %d, Completed Jobs: %d, Total Jobs: %d", e.getActiveCount(), e.getCompletedTaskCount(), e.getTaskCount()));
        l.info(String.format("\t Shutdown? %s, Terminated? %s, Terminating? %s", e.isShutdown(), e.isTerminated(), e.isTerminating()));
        l.info("*End Toast Thread Pool Data");
    }

    /**
     * Returns a help message to display with the 'help' command
     */
    @Override
    public String getHelp() {
        return "Prints monitoring data regarding the ToastThreadPool. ";
    }
}
