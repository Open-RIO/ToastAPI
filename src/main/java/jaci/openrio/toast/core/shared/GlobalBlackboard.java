package jaci.openrio.toast.core.shared;

import java.util.HashMap;

/**
 * The GlobalBlackboard is a {@link java.util.HashMap} with a single instance that allows
 * for data to be shared globally among all Modules and Toast itself. This is used to store
 * data throughout the entire Toast session that can be read at any time.
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
