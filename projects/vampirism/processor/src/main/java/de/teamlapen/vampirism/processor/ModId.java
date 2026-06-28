package de.teamlapen.vampirism.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Required companion to {@link ConvertedCreature} / {@link AdditionalConverter}. The value is used
 * as the mod ID for {@code DeferredRegister}, {@code @EventBusSubscriber}, and datagen provider
 * registration in the generated classes, and is embedded in the generated class names so that
 * multiple mods can coexist in the same compilation unit.
 *
 * <pre>{@code
 * @ModId("vampirism")
 * @ConvertedCreature(value = Cow.class, width = 0.9F, height = 1.4F)
 * public final class GeneratedConvertedCreatures {}
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ModId {
    String value();
}
