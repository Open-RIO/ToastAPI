package jaci.openrio.toast.core.command;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.command.cmd.*;

import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.function.Function;

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

    public static List<AbstractCommand> commands = new Vector<>();
    public static List<FuzzyCommand> parsers = new Vector<>();

    /**
     * Start the command bus. This is done by Toast, this shouldn't be triggered by Modules.
     * This also starts the Simulation Scanner to listen from an IDE's console
     */
    public static void init() {
        if (ToastBootstrap.isSimulation)
            setupSim();

        registerNatives();
    }

    /**
     * Register the default commands that come with Toast by default.
     */
    private static void registerNatives() {
        registerCommand(new CommandUSB());
        registerCommand(new CommandThreadPool());
        registerCommand(new CommandInvokeCrash());
        registerCommand(new CommandList());
        registerCommand(new CommandHelp());
        registerCommand(new CommandExit());
        registerCommand(new CommandProfiler());
        registerCommand(new CommandReloadConfigs());
        registerCommand(new CommandJScript());
    }

    /**
     * Parse the given message and invoke any commands matching it
     */
    public static void parseMessage(String message) {
        ln = message;
        try {
            if (messageRequested()) {
                synchronized (requestLock) {
                    requestLock.notify();
                }
                return;
            }
        } catch (Exception e) {
            Toast.log().error("Could not do Custom Message implementation -- Falling back to regular command bus");
            Toast.log().exception(e);
        }

        new Thread() {
            public void run() {
                this.setName("Command-Execution");
                inCommand = true;
                boolean commandFound = false;
                String[] split = message.split(" ");
                for (AbstractCommand command : commands) {
                    boolean alias = false;
                    String[] aliases = command.getAlias();
                    if (aliases != null) {
                        for (String a : aliases)
                            if (a.equals(split[0])) alias = true;
                    }
                    if (split[0].equals(command.getCommandName()) || alias) {
                        try {
                            String[] newSplit = new String[split.length - 1];
                            System.arraycopy(split, 1, newSplit, 0, split.length - 1);
                            command.invokeCommand(newSplit.length, newSplit, message);
                        } catch (UsageException e) {
                            Toast.log().warn(e.getMessage());
                        } catch (Exception e) {
                            Toast.log().error("Error while executing command: " + e);
                            Toast.log().exception(e);
                        }
                        commandFound = true;
                    }
                }

                for (FuzzyCommand command : parsers) {
                    if (command.shouldInvoke(message)) {
                        try {
                            command.invokeCommand(message);
                        } catch (UsageException e) {
                            Toast.log().warn(e.getMessage());
                        } catch (Exception e) {
                            Toast.log().error("Error while executing command: " + e);
                            Toast.log().exception(e);
                        }
                        commandFound = true;
                    }
                }

                if (!commandFound) {
                    Toast.log().warn("Command not found");
                }
                inCommand = false;
            }
        }.start();
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

    static boolean nextLineRequested = false;
    static boolean inCommand = false;
    static Scanner scanner;
    static String ln;
    static final Object requestLock = new Object();
    static Thread t;

    /**
     * Will bypass the CommandBus and give the next Console line or Command Message to you. Use this
     * to get data from the user. Keep in mind this is a blocking method.
     *
     * @throws InterruptedException Something wrong happened. This should never trigger
     */
    public static String requestNextMessage() throws InterruptedException {
        synchronized (requestLock) {
            nextLineRequested = true;
            requestLock.wait();
            nextLineRequested = false;
            return ln;
        }
    }

    /**
     * Returns true if the {@link #requestNextMessage} has been called
     */
    public static boolean messageRequested() {
        return nextLineRequested;
    }

    /**
     * Setup the simulation environment scanner for the IDE's console
     */
    private static void setupSim() {
        scanner = new Scanner(System.in);
        t = new Thread() {
            public void run() {
                Thread.currentThread().setName("Toast|Sim");
                try {
                    while (Thread.currentThread().isAlive()) {
                        ln = scanner.nextLine();
                        parseMessage(ln);
                    }
                } catch (Exception e) {}
            };
        };
        t.start();
    }

}
