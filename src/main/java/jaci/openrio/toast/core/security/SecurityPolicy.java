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

    public static Logger log() {
        if (log == null) log = new Logger("Security", Logger.ATTR_DEFAULT);
        return log;
    }

    public static SecurityPolicy match(String s) {
        for (SecurityPolicy policy : values()) {
            if (policy.name().equalsIgnoreCase(s)) return policy;
        }
        return STRICT;
    }

    public static SecurityPolicy get() {
        if (policy != null) return policy;
        String s = ToastConfiguration.Property.SECURITY_POLICY.asString();
        policy = match(s);
        return policy;
    }

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
