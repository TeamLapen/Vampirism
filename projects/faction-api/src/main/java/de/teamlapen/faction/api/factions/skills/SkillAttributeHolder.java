package de.teamlapen.faction.api.factions.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.function.Supplier;

public record SkillAttributeHolder(Identifier skillId, Holder<Attribute> attribute, Supplier<Double> amountSupplier, AttributeModifier.Operation operation) {

    public AttributeModifier create() {
        return new AttributeModifier(this.skillId(), this.amountSupplier.get(), this.operation);
    }
}