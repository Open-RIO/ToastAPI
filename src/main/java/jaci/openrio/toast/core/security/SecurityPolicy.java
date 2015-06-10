package jaci.openrio.toast.core.security;

import jaci.openrio.toast.core.ToastConfiguration;
import jaci.openrio.toast.lib.log.Logger;

import java.security.Permission;

public enum SecurityPolicy {
    NONE, LOOSE, STRICT;

    static Logger log;
    static SecurityPolicy policy;

    public static Logger getLogger() {
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
            log.warn("Toast Security Manager: " + message);
        } else {
            log.warn("Toast Security Manager: " + message);
            throw new SecurityException(message);
        }
    }
}
