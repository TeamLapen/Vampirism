package de.teamlapen.vampirism.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which allows to specify for float type an allowed values range.
 * Applying this annotation to other types is not correct.
 * <p>
 * Example:
 * <pre>{@code public @FloatRange(from = 0, to = Float.MAX_VALUE) float length() {
 *   return this.length; // returns a non-negative float
 * }}</pre>
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE})
public @interface FloatRange {

    float from() default Float.MIN_VALUE;

    float to() default Float.MAX_VALUE;
}
