package de.teamlapen.faction.api.factions.refinements;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * A refinement that can be applied to refinement items {@link de.teamlapen.faction.api.world.items.IRefinementItem}
 */
public interface IRefinement {


    /**
     * If this refinement modifies an attribute, this factory is used to create the attribute modifier for the {@link #getAttribute()} with {@link #getModifierValue()}
     */
    @Nullable
    @Contract(pure = true)
    BiFunction<Identifier, Double, AttributeModifier> attributeFactory();

    /**
     * The optional attribute that this refinement can modify
     */
    @Nullable
    Holder<Attribute> getAttribute();

    /**
     * A mutable description of this refinement
     */
    default MutableComponent getDescription() {
        return Component.translatable(getDescriptionId());
    }

    /**
     * The description id for this refinement
     */
    String getDescriptionId();

    double getModifierValue();
}
