package jaci.openrio.toast.core.loader.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The child classes of {@link jaci.openrio.toast.core.loader.annotation.Tree}
 *
 * Branches are classes that are loaded as optionals. Branches specify a Module name or a Class name. If that
 * module or class exists, the Branch class is loaded and the method defined called. If not, the branch is ignored.
 *
 * This allows for modules to have 'optional' dependencies. Classes can have multiple Branch annotations on them, allowing
 * for one class to be responsible for all optional loading.
 *
 * This annotation, Branch, is placed on your 'Branch' classes, classes that are optionally loaded depending on whether a module
 * or class is present or not. Your class can have multiple of these annotations.
 *
 * @author Jaci
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Branch {

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
}
