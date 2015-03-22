package jaci.openrio.toast.core.command.cmd

import jaci.openrio.toast.core.Toast
import jaci.openrio.toast.core.command.FuzzyCommand
import jaci.openrio.toast.core.shared.GlobalBlackboard

class CommandGroovyScript extends FuzzyCommand {

    @Override
    boolean shouldInvoke(String message) {
        return message.startsWith("script")
    }

    @Override
    void invokeCommand(String message) {
        String groovy = message.replaceFirst("script", "")
        Binding binding = new Binding();
        binding.setVariable("_global", GlobalBlackboard.INSTANCE)
        binding.setVariable("_toast", Toast.getToast())
        GroovyShell shell = new GroovyShell(binding)
        shell.evaluate(groovy)
    }
}
