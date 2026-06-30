package de.teamlapen.vampirism.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a vanilla→converted entity mapping for entities whose converted type is registered outside
 * the {@link ConvertedCreature} system (e.g. {@code VillagerConvertedEntity}). The processor includes
 * these in the generated {@code GeneratedConvertedEntitiesData} DataProvider alongside the entries
 * derived from {@link ConvertedCreature}, so no handwritten datagen code is needed.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(AdditionalConverters.class)
public @interface AdditionalConverter {

    /**
     * The vanilla entity class. Its simple name is upper-cased to derive the
     * {@code EntityType} constant (e.g. {@code Villager.class} → {@code EntityType.VILLAGER}).
     */
    Class<?> vanilla();

    /**
     * Fully-qualified reference to the {@code DeferredHolder} field for the converted entity type,
     * e.g. {@code "de.teamlapen.vampirism.common.core.ModEntities.VILLAGER_CONVERTED"}.
     * The processor splits at the last {@code '.'} to get the class FQN and field name.
     */
    String convertedField();
}
