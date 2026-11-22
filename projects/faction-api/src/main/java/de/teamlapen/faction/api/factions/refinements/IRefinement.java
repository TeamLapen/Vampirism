package de.teamlapen.faction.api.factions.refinements;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

/**
 * A refinement that can be applied to refinement items {@link de.teamlapen.faction.api.world.items.IRefinementItem}
 * <p>
 * Refinements are loaded from datapacks. Registry key {@link de.teamlapen.faction.api.FactionRegistries.Keys#REFINEMENT}
 */
public interface IRefinement {

    /**
     * The optional attribute that this refinement modifies
     */
    @Nullable
    Holder<Attribute> getAttribute();

    /**
     * Creates the attribute modifier for {@link #getAttribute()}
     *
     * @param id the id for the modifier. Usually the id of the refinement
     * @return the modifier or null if this refinement does not modify an attribute
     */
    @Nullable
    AttributeModifier createAttributeModifier(Identifier id);

    /**
     * The description of this refinement
     */
    Component getDescription();
}
