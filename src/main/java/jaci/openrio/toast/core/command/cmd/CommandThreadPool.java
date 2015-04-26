package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.AbstractCommand;
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
public class CommandThreadPool extends AbstractCommand {

    @Override
    public String getCommandName() {
        return "threads";
    }

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
}
