package jaci.openrio.toast.core.loader.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Branches are classes that are loaded as optionals. Branches specify a Module name or a Class name. If that
 * module or class exists, the Branch class is loaded and the method defined called. If not, the branch is ignored.
 *
 * This allows for modules to have 'optional' dependencies. Classes can have multiple Branch annotations on them, allowing
 * for one class to be responsible for all optional loading.
 *
 * This annotation, Branch, is placed on your Main Module class. If the dependency() class or module exists, then the branch()
 * class is invoked with the method() method.
 *
 * @author Jaci
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Branch {

    /**
     * The fully quantified class name of the branch class to use in loading.
     *
     * This is the class to be loaded if the dependency() argument is met
     */
    String branch();

    /**
     * Either:
     * a) A module name.
     * or
     * b) A fully quantified class name.
     *
     * If the module or class defined is found in the Toast classpath, this class is loaded and the public static
     * method defined in {@link #method()} is invoked.
     */
    String dependency();

    /**
     * The name of a public static method in this class that will be invoked if the dependency is found. This
     * method should take no arguments.
     */
    String method();

    /**
     * Should the method be immediately executed? If true, the method won't be hit at prestart, but will, however, be executed
     * as soon as the annotation is constructed
     */
    boolean immediate() default false;
}
