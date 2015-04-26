package jaci.openrio.toast.core.command.cmd;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.FuzzyCommand;
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
public class CommandGroovyScript extends FuzzyCommand {
    @Override
    public boolean shouldInvoke(String message) {
        return message.startsWith("script ") || message.startsWith("script -c");
    }

    @Override
    public void invokeCommand(String message) {
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
    }

}
