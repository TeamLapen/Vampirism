package de.teamlapen.vampirism.api.entity.player.refinement;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IRefinement {


    @Nullable
    AttributeModifier createAttributeModifier(double value);

    @Nullable
    Holder<Attribute> getAttribute();

    @NotNull
    Component getDescription();

    double getModifierValue();
}
