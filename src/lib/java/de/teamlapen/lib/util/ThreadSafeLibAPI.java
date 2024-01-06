package de.teamlapen.lib.util;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any method annotated with this may be called during {@link net.neoforged.fml.event.lifecycle.InterModEnqueueEvent} without the risk of concurrency issues.
 * Any method not annotated with this must not be called during that phase
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ThreadSafeLibAPI {
}
