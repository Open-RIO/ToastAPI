package jaci.openrio.toast.core.command;

/**
 * An exception for a Command to Throw if the usage of the command (the args) is not satisfactory. This will cause a console
 * log of the error.
 *
 * @author Jaci
 */
public class UsageException extends RuntimeException {

    String usage;

    public UsageException(String usage) {
        super();
        this.usage = usage;
    }

    public String getUsage() {
        return usage;
    }

    public String getMessage() {
        return "Invalid command usage. " + usage;
    }

}
