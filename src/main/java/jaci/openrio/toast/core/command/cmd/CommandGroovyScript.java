package jaci.openrio.toast.core.command.cmd;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.FuzzyCommand;
import jaci.openrio.toast.core.command.IHelpable;
import jaci.openrio.toast.core.shared.GlobalBlackboard;
import jaci.openrio.toast.core.thread.ToastThreadPool;

/**
 * This command is used for on-the-fly Groovy Scripting. For the most part, this is useless, but it's both fun to play
 * around with, useful for debugging, and freaking awesome.
 *
 * command_name: 'script'
 * args:
 *      -c  Execute the script in the {@link jaci.openrio.toast.core.thread.ToastThreadPool}, concurrently
 *
 * @author Jaci
 */
public class CommandGroovyScript extends FuzzyCommand implements IHelpable {

    /**
     * Should this Command be invoked with the given message?
     */
    @Override
    public boolean shouldInvoke(String message) {
        return message.startsWith("script ") || message.startsWith("script -c");
    }

    /**
     * Invokes the command if {@link #shouldInvoke} returns true.
     * @param message The full command message. This is left un-parsed so you can handle
     *                it yourself
     */
    @Override
    public void invokeCommand(String message) {
        try {
            String groovy;
            boolean concurrent = false;
            if (message.startsWith("script -c")) {
                groovy = message.replaceFirst("script -c", "");
                concurrent = true;
            } else groovy = message.replaceFirst("script", "");

            Binding binding = new Binding();
            binding.setVariable("_global", GlobalBlackboard.INSTANCE);
            binding.setVariable("_toast", Toast.getToast());
            GroovyShell shell = new GroovyShell(binding);
            if (concurrent)
                ToastThreadPool.INSTANCE.addWorker(new Runnable() {
                    @Override
                    public void run() {
                        shell.evaluate(groovy);
                    }
                });
            else shell.evaluate(groovy);
        } catch (Exception e) {
            Toast.log().error("Failed to execute script");
            Toast.log().exception(e);
        }
    }

    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "script";
    }

    /**
     * Returns a help message to display with the 'help' command
     */
    @Override
    public String getHelp() {
        return "Executes a groovy script on-the-fly. Run with -c to execute concurrently. Example: \"script -c println 'Hello World'\"";
    }
}
