package de.teamlapen.vampirism.processor;

import org.jspecify.annotations.Nullable;

import javax.lang.model.element.Element;

/**
 * Data model for a single {@link ConvertedCreature} annotation, with all defaults already resolved.
 *
 * <p>{@code width}/{@code height} may be {@code -1f} to signal "use entity type dimensions" — only
 * {@link de.teamlapen.vampirism.processor.generator.RegistrarGenerator} interprets this sentinel.
 */
public record CreatureModel(
        Element element,
        String packageName,
        String modId,
        String baseFqn,
        String baseSimple,
        String className,
        String entityType,
        String registryName,
        String holderField,
        float width,
        float height,
        String mobCategory,
        String attributeMethod,
        String spawnRulesFrom,
        boolean immuneToSweetBerryBush,
        @Nullable String subclassFqn,
        String registeredSimple,
        @Nullable String rendererFqn,
        @Nullable String rendererSimple
) {
    public boolean hasSubclass() {
        return subclassFqn != null;
    }

    public boolean hasRenderer() {
        return rendererFqn != null;
    }
}
