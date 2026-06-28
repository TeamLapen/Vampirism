package de.teamlapen.vampirism.processor;

/**
 * Data model for a single {@link AdditionalConverter} annotation, with all fields resolved.
 *
 * @param entityType       {@code EntityType} constant name, e.g. {@code "VILLAGER"}
 * @param converterClassFqn fully-qualified class that holds the {@code DeferredHolder} field,
 *                         e.g. {@code "de.teamlapen.vampirism.common.core.ModEntities"}
 * @param converterSimple  simple class name, e.g. {@code "ModEntities"}
 * @param converterField   field name, e.g. {@code "VILLAGER_CONVERTED"}
 */
public record AdditionalConverterModel(
        String modId,
        String entityType,
        String converterClassFqn,
        String converterSimple,
        String converterField
) {}
