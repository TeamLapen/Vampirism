package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IAttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collection;

@Deprecated
public interface IAttributeInstanceVampirismMock extends IAttributeInstance {
    @Override
    default Collection<AttributeModifier> invokeGetModifiersOrEmpty(AttributeModifier.Operation operation) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
