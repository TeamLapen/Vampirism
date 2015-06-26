package de.teamlapen.vampirism.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface DefaultDouble {
	String comment() default "";

	double maxValue() default Double.MAX_VALUE;

	double minValue() default Double.MIN_VALUE;

	String name();

	double value();
}
