package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collection;

public interface IAttributeInstance {

    Collection<AttributeModifier> invokeGetModifiersOrEmpty(AttributeModifier.Operation operation);
}
