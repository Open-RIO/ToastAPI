package jaci.openrio.toast.core.script.js;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The JSON Annotation, when placed above a method or field, will signify to the {@link JavaScript} class and
 * {@link JsonEngine} that a field can be serialized. The field's current value will be taken, or, if placed on a
 * method, the method will be invoked and the return value used as the value. The method MUST NOT take any
 * arguments.
 *
 * @author Jaci
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JSON {

    String name() default "{DEFAULT}";

}
