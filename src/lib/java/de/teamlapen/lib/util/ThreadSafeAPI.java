package de.teamlapen.lib.util;

import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any method annotated with this may be called during {@link InterModEnqueueEvent} without the risk of concurrency issues.
 * Any method not annotated with this must not be called during that phase
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ThreadSafeAPI {
}
