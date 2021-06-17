package de.teamlapen.vampirism.api.entity.player.refinement;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface IRefinement extends IForgeRegistryEntry<IRefinement> {


    @Nullable
    AttributeModifier createAttributeModifier(UUID uuid, double value);

    @Nullable
    Attribute getAttribute();

    @Nonnull
    ITextComponent getDescription();

    double getModifierValue();

    @Nullable
    UUID getUUID();
}
