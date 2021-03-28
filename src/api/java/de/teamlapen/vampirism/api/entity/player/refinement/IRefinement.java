package de.teamlapen.vampirism.api.entity.player.refinement;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface IRefinement extends IForgeRegistryEntry<IRefinement> {

    @Nonnull
    TYPE getType();

    @Nullable
    AttributeModifier createAttributeModifier(UUID uuid);

    @Nullable
    Attribute getAttribute();

    @Nonnull
    String getDescriptionKey();

    enum TYPE {
        SKILL, ATTRIBUTE
    }
}
