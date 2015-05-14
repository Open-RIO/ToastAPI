package jaci.openrio.toast.core.loader.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * List a priority for a standard module method to be triggered in. This allows for you to predict when a method
 * will be executed instead of them being seemingly random per method. The default is NORMAL (0)
 *
 * @author Jaci
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Priority {

    /**
     * The priority level to execute on. This can be any double value, MIN_VALUE being the lowest priority and MAX_VALUE
     * being the highest, with 0 being indifferent. The highest priority values will be executed First, and the lowest
     * executed last.
     */
    double level();

    public static class Level {
        public static final double LOWEST = -1;
        public static final double LOWER = -0.5;
        public static final double LOW = -0.25;
        public static final double NORMAL = 0;
        public static final double HIGH = 0.25;
        public static final double HIGHER = 0.5;
        public static final double HIGHEST = 1;
    }

}
