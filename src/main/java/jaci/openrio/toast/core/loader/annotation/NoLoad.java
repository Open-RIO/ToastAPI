package jaci.openrio.toast.core.loader.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Placing this annotation on a {@link jaci.openrio.toast.lib.module.ToastModule} class will cause the loader to not
 * recognize the module as a valid candidate. This is used for ToastModules that are used as abstract classes or designed
 * to be extended by other classes. This should be placed above the class definition.
 *
 * @author Jaci
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NoLoad { }
