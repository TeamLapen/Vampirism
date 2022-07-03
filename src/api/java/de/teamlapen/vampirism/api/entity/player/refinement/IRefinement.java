package de.teamlapen.vampirism.api.entity.player.refinement;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface IRefinement {


    @Nullable
    AttributeModifier createAttributeModifier(UUID uuid, double value);

    @Nullable
    Attribute getAttribute();

    @Nonnull
    Component getDescription();

    double getModifierValue();

    @Nullable
    UUID getUUID();
}
