package jaci.openrio.toast.core.command;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.command.cmd.CommandGroovyScript;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The main pipeline for commands. This bus handles registering and invocation of commands. This allows for
 * robot functions to be triggered remotely, or locally, based on a message.
 *
 * @see jaci.openrio.toast.core.command.AbstractCommand
 * @see jaci.openrio.toast.core.command.FuzzyCommand
 *
 * @author Jaci
 */
public class CommandBus {

    private static List<AbstractCommand> commands;
    private static List<FuzzyCommand> parsers;

    public static void init() {
        commands = new ArrayList<>();
        parsers = new ArrayList<>();

        if (ToastBootstrap.isSimulation)
            setupSim();

        registerNatives();
    }

    private static void registerNatives() {
        registerCommand(new CommandGroovyScript());
    }

    /**
     * Parse the given message and invoke any commands matching it
     */
    public static void parseMessage(String message) {
        String[] split = message.split(" ");
        for (AbstractCommand command : commands) {
            if (split[0].equals(command.getCommandName())) {
                String[] newSplit = new String[split.length - 1];
                System.arraycopy(split, 1, newSplit, 0, split.length-1);
                command.invokeCommand(split.length, newSplit, message);
            }
        }

        for (FuzzyCommand command : parsers) {
            if (command.shouldInvoke(message))
                command.invokeCommand(message);
        }
    }

    /**
     * Register a {@link jaci.openrio.toast.core.command.FuzzyCommand} on the bus
     */
    public static void registerCommand(FuzzyCommand command) {
        parsers.add(command);
    }

    /**
     * Register a {@link jaci.openrio.toast.core.command.AbstractCommand} on the bus
     */
    public static void registerCommand(AbstractCommand command) {
        commands.add(command);
    }

    private static void setupSim() {
        Scanner scanner = new Scanner(System.in);
        Thread thread = new Thread() {
            public void run() {
                Thread.currentThread().setName("Toast|SimulationCommands");
                try {
                    while (Thread.currentThread().isAlive()) {
                        parseMessage(scanner.nextLine());
                    }
                } catch (Exception e) {}
            };
        };
        thread.start();
    }

}
