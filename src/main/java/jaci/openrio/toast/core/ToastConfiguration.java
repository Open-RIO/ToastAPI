package jaci.openrio.toast.core;

import jaci.openrio.toast.core.loader.groovy.GroovyPreferences;
import jaci.openrio.toast.lib.math.MathHelper;

/**
 * The configuration manager for Toast's config file. This is handled by Toast and does not concern external modules.
 *
 * @author Jaci
 */
public class ToastConfiguration {

    static GroovyPreferences preferences;

    /**
     * Initialize the Configuration. This creates the Preferences file
     * and all the properties required.
     */
    public static void init() {
        preferences = new GroovyPreferences("Toast");
        for (Property prop : Property.values())
            prop.read(preferences);
    }

    /**
     * Jet fuel can't melt steel beams.
     */
    public static void jetfuel() {
        String fuel = Property.JET_FUEL.asString();
        if (!fuel.equalsIgnoreCase("steel beams"))
            Toast.log().info("Jet Fuel Can't Melt " + fuel);
    }

    public static enum Property {
        THREAD_POOL_SIZE("threading.pool_size", 4, "The amount of threads in the Thread Pool. The more threads, the more concurrent operations. This should be kept at 4 for most cases, but can be bumped up if required."),
        THREADED_LOADING("experimental.threaded_loading", false, "Should module loading be threaded? This has the advantage of speeding up loading times, but may be unstable for modules which aren't properly configured for concurrency."),
        //THREADED_TICKING("experimental.threaded_ticking", false, "Should ticking by the StateTracker (autonomous, teleop, test, disabled, etc), be Threaded for each listener? This makes ticking run quickly and concurrently, but may be unstable for modules that aren't properly configured for concurrent operations."),

        COMMANDS_DELEGATE_PASSWORD("delegate.command.password", "", "The password to use for the Command Bus Delegate"),
        COMMANDS_DELEGATE_ALGORITHM("delegate.command.algorithm", "SHA256", "The algorithm used to hash the Command Bus delegate password with. Default: SHA256, Options: [SHA1, MD5, SHA256]"),
        LOGGER_DELEGATE_PASSWORD("delegate.logger.password", "", "The password to use for the Logger delegate"),
        LOGGER_DELEGATE_ALGORITHM("delegate.logger.algorithm", "SHA256", "The algorithm used to hash the Logger delegate password with. Default: SHA256, Options: [SHA1, MD5, SHA256]"),

        OPTIMIZATION_GC("optimization.gc.enabled", false, "Should Toast regularly perform System Garbage Collection every 'optimization.gc.time' seconds? This allows the RAM Heap Space to be freed more regularly, but at the cost of a little bit of performance every GC cycle. This may or may not change performance for RAM-hungry applications"),
        OPTIMIZATION_GC_TIME("optimization.gc.time", 30, "The time per Garbage Collection cycle"),

        SECURITY_POLICY("security.policy", "STRICT", "The Security Policy to use with the SecurityManager. There are 3 possible values: NONE, LOOSE and STRICT. NONE will ignore any violations, LOOSE will warn in the Logger, and STRICT will deny access."),

        JET_FUEL("jet.fuel", "Steel Beams", "JET FUEL CAN'T MELT STEEL BEAMS"),
        ;

        String key;
        Object defaultValue;
        String[] comments;

        Object value;

        Property(String key, Object defaultValue, String comment) {
            if (key.startsWith("experimental."))
                comment = "WARNING: EXPERIMENTAL CODE. DO NOT USE IN COMPETITION UNLESS YOU KNOW WHAT YOU ARE DOING! " + comment;
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = MathHelper.splitStringByWords(comment, 15);
        }

        /**
         * Read the property from the GroovyPreferences file, with the default value and comments
         * if it doesn't exist.
         */
        public void read(GroovyPreferences preferences) {
            value = preferences.getObject(key, defaultValue, comments);
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
