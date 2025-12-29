package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IAttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(AttributeInstance.class)
public interface AttributeInstanceAccessor extends IAttributeInstance {

    @Override
    @Invoker("getModifiersOrEmpty")
    Collection<AttributeModifier> invokeGetModifiersOrEmpty(AttributeModifier.Operation operation);
}
