package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(AttributeInstance.class)
public interface AttributeInstanceAccessor {

    @Invoker("removeModifier")
    void invokeRemoveModifier(AttributeModifier pModifier);

    @Invoker("getModifiersOrEmpty")
    Collection<AttributeModifier> invokeGetModifiersOrEmpty(AttributeModifier.Operation operation);
}
