package jaci.openrio.toast.core.command.cmd;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.command.FuzzyCommand;
import jaci.openrio.toast.core.shared.GlobalBlackboard;

public class CommandGroovyScript extends FuzzyCommand {
    @Override
    public boolean shouldInvoke(String message) {
        return message.startsWith("script");
    }

    @Override
    public void invokeCommand(String message) {
        String groovy = message.replaceFirst("script", "");
        Binding binding = new Binding();
        binding.setVariable("_global", GlobalBlackboard.INSTANCE);
        binding.setVariable("_toast", Toast.getToast());
        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(groovy);
    }

}
