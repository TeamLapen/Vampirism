package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IAttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collection;
import java.util.List;

public interface IAttributeInstanceMock extends IAttributeInstance {
    @Override
    default Collection<AttributeModifier> invokeGetModifiersOrEmpty(AttributeModifier.Operation operation) {
        return List.of();
    }
}
