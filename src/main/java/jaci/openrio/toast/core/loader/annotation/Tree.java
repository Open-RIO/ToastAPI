package jaci.openrio.toast.core.loader.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The main stem for {@link jaci.openrio.toast.core.loader.annotation.Branch} classes.
 * Branches are classes that are loaded as optionals. Branches specify a Module name or a Class name. If that
 * module or class exists, the Branch class is loaded and the method defined called. If not, the branch is ignored.
 *
 * This allows for modules to have 'optional' dependencies. Classes can have multiple Branch annotations on them, allowing
 * for one class to be responsible for all optional loading.
 *
 * This annotation, Tree, is put on your ToastModule class. This annotation connects all your branch classes for Toast to
 * load. Your ToastModule class can have multiple Tree annotations.
 *
 * @author Jaci
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Tree {

    /**
     * The fully quantified class name of a branch (e.g. my.module.MyBranch)
     */
    String branch();
}