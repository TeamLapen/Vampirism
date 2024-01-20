package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AttributeInstance.class)
public interface AttributeInstanceAccessor {

    @Invoker("removeModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V")
    void invoke_removeModifier(AttributeModifier pModifier);
}
