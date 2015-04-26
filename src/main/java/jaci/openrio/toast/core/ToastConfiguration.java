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

    public static void init() {
        preferences = new GroovyPreferences("Toast");
        for (Property prop : Property.values())
            prop.read(preferences);
    }

    public static void jetfuel() {
        String fuel = Property.JET_FUEL.asString();
        if (!fuel.equalsIgnoreCase("steel beams"))
            Toast.log().info("Jet Fuel Can't Melt " + fuel);
    }

    public static enum Property {
        THREAD_POOL_SIZE("threading.pool_size", 2, "The amount of threads in the Thread Pool. The more threads, the more concurrent operations. This should be kept at 2 for most cases, but can be bumped up if required."),
        THREADED_LOADING("experimental.threaded_loading", false, "Should module loading be threaded? This has the advantage of speeding up loading times, but may be unstable for modules which aren't properly configured for concurrency."),
        //THREADED_TICKING("experimental.threaded_ticking", false, "Should ticking by the StateTracker (autonomous, teleop, test, disabled, etc), be Threaded for each listener? This makes ticking run quickly and concurrently, but may be unstable for modules that aren't properly configured for concurrent operations."),

        COMMANDS_DELEGATE_PASSWORD("delegate.command.password", "", "The password to use for the Command Bus Delegate"),
        COMMANDS_DELEGATE_ALGORITHM("delegate.command.algorithm", "SHA256", "The algorithm used to hash the Command Bus delegate password with. Default: SHA256, Options: [SHA1, MD5, SHA256]"),
        LOGGER_DELEGATE_PASSWORD("delegate.logger.password", "", "The password to use for the Logger delegate"),
        LOGGER_DELEGATE_ALGORITHM("delegate.logger.algorithm", "SHA256", "The algorithm used to hash the Logger delegate password with. Default: SHA256, Options: [SHA1, MD5, SHA256]"),

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

        public void read(GroovyPreferences preferences) {
            value = preferences.getObject(key, defaultValue, comments);
        }

        public Number asNumber() {
            return (Number) defaultValue;
        }

        public int asInt() {
            return asNumber().intValue();
        }

        public double asDouble() {
            return asNumber().doubleValue();
        }

        public float asFloat() {
            return asNumber().floatValue();
        }

        public byte asByte() {
            return asNumber().byteValue();
        }

        public String asString() {
            return (String) value;
        }

        public boolean asBoolean() {
            return (boolean) value;
        }
    }

}
