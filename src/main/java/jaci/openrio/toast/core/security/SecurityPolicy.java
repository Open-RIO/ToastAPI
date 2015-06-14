package jaci.openrio.toast.core.security;

import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.lib.log.Logger;

import java.security.Permission;

/**
 * The SecurityPolicy enumeration containing all the possible values for controlling the verbosity and action of the
 * {@link ToastSecurityManager}. This dictates how strictly the security manager is controlled, and is defined in the
 * Toast configuration file. More information is defined in the {@link ToastSecurityManager}
 *
 * @author Jaci
 */
public enum SecurityPolicy {
    NONE, LOOSE, STRICT;

    static Logger log;
    static SecurityPolicy policy;

    /**
     * Get the SecurityPolicy logger, or create it if it doesn't already exist.
     */
    public static Logger log() {
        if (log == null) log = new Logger("Security", Logger.ATTR_DEFAULT);
        return log;
    }

    /**
     * Match the String provided to the Enumerated values. This is used to parse the data
     * provided in the ToastConfiguration
     */
    public static SecurityPolicy match(String s) {
        for (SecurityPolicy policy : values()) {
            if (policy.name().equalsIgnoreCase(s)) return policy;
        }
        return STRICT;
    }

    /**
     * Get the currently active security policy, or read it from the Configuration if it
     * isn't set.
     */
    public static SecurityPolicy get() {
        if (policy != null) return policy;
        String s = ToastConfiguration.Property.SECURITY_POLICY.asString();
        policy = match(s);
        return policy;
    }

    /**
     * Trigger the policy if there is a violation in the {@link SecurityManager}
     */
    public void trigger(Permission perm, String message) {
        if (this.equals(NONE)) return;
        if (this.equals(LOOSE)) {
            log().warn(message);
            log().exception(new Throwable());
        } else {
            log().warn(message);
            log().exception(new Throwable());
            throw new SecurityException(message);
        }
    }
}
