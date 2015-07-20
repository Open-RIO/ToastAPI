package jaci.openrio.toast.core;

import jaci.openrio.toast.lib.module.ModuleConfig;

/**
 * The configuration manager for Toast's config file. This is handled by Toast and does not concern external modules.
 *
 * @author Jaci
 */
public class ToastConfiguration {

    public static ModuleConfig config;

    /**
     * Initialize the Configuration. This creates the Preferences file
     * and all the properties required.
     */
    public static void init() {
        config = new ModuleConfig("Toast");
        for (Property prop : Property.values())
            prop.read(config);
    }

    public static enum Property {
        THREAD_POOL_SIZE("threading.pool_size", 4),
        THREADED_LOADING("experimental.threaded_loading", false),

        COMMANDS_DELEGATE_PASSWORD("delegate.command.password", ""),
        COMMANDS_DELEGATE_ALGORITHM("delegate.command.algorithm", "SHA256"),
        LOGGER_DELEGATE_PASSWORD("delegate.logger.password", ""),
        LOGGER_DELEGATE_ALGORITHM("delegate.logger.algorithm", "SHA256"),

        OPTIMIZATION_GC("optimization.gc.enabled", false),
        OPTIMIZATION_GC_TIME("optimization.gc.time", 30),

        SECURITY_POLICY("security.policy", "STRICT"),

        ROBOT_NAME("robot.name", "{ NAME NOT SET }"),
        ROBOT_TEAM("robot.team", -1),
        ROBOT_DESC("robot.desc", "{ DESCRIPTION NOT SET }")
        ;

        String key;
        Object defaultValue;

        Object value;

        Property(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        /**
         * Read the property from the ModuleConfig file, with the default value and comments
         * if it doesn't exist.
         */
        public void read(ModuleConfig config) {
            value = config.get(key, defaultValue);
        }

        /**
         * Get the property as a Number object
         */
        public Number asNumber() {
            return (Number) value;
        }

        /**
         * Get the property, casted to an Integer
         */
        public int asInt() {
            return asNumber().intValue();
        }

        /**
         * Get the property casted to a Double
         */
        public double asDouble() {
            return asNumber().doubleValue();
        }

        /**
         * Get the property, casted to a Float
         */
        public float asFloat() {
            return asNumber().floatValue();
        }

        /**
         * Get the property, casted to a Byte
         */
        public byte asByte() {
            return asNumber().byteValue();
        }

        /**
         * Get the property in String form
         */
        public String asString() {
            return (String) value;
        }

        /**
         * Get the property as a boolean.
         */
        public boolean asBoolean() {
            return (boolean) value;
        }
    }

}
