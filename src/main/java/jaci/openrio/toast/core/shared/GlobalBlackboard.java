package jaci.openrio.toast.core.shared;

import java.util.HashMap;

/**
 * The GlobalBlackboard is a {@link java.util.HashMap} with a single instance that allows
 * for data to be shared more generally than the {@link jaci.openrio.toast.core.loader.module.ModuleStorage} object.
 *
 * This class is intended to be used by the {@link jaci.openrio.toast.core.command.cmd.CommandGroovyScript} class to
 * allow variables to be altered simply, and can be accessed from the Script command using '_global'
 *
 * @author Jaci
 */
public class GlobalBlackboard extends HashMap<String, Object> {

    public static GlobalBlackboard INSTANCE;

    public GlobalBlackboard() {
        super();
        INSTANCE = this;
    }

}
