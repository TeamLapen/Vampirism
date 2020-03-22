package de.teamlapen.lib.lib.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DefaultDouble {
    double alternateValue() default 0D;

    String comment() default "";

    /**
     * Whether this default value has an alternate or not.
     * Used if two alternative default configurations shall provided. (E.g. Realism mode)
     * Necessary because the default for the alternative value cannot be null
     *
     * @return
     */
    boolean hasAlternate() default false;

    double maxValue() default Double.POSITIVE_INFINITY;

    double minValue() default Double.NEGATIVE_INFINITY;

    String name() default "";

    double value();
}