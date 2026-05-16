package de.teamlapen.integration;

import net.neoforged.api.distmarker.Dist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// # Annotation for integration providers
/// This work similar to [net.neoforged.fml.common.Mod] and [net.neoforged.fml.common.EventBusSubscriber]
///
/// Annotate a class with this annotation to register it as an integration provider. The class is automatically picked up by the integration loader.
///
/// An object of the annotated class is created during the mod loading process and registered to the mod event bus of the owning mod.
/// This only supports static event subscriber.
///
/// Classes that use this annotation are not loaded if the mod is not loaded. Be careful with accessing it
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Integration {

    String modId();

    Dist[] dist() default { Dist.CLIENT, Dist.DEDICATED_SERVER };
}
