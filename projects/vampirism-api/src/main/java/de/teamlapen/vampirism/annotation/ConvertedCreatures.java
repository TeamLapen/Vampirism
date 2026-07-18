package de.teamlapen.vampirism.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for the {@link java.lang.annotation.Repeatable repeatable} {@link ConvertedCreature}.
 * You normally never reference this directly.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ConvertedCreatures {
    ConvertedCreature[] value();
}
