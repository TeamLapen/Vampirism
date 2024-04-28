package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Mixin(AttributeInstance.class)
public interface AttributeInstanceAccessor {

    @Invoker("removeModifier")
    void invoke_removeModifier(AttributeModifier pModifier);

    @Invoker("getModifiersOrEmpty")
    Collection<AttributeModifier> getModifiers(AttributeModifier.Operation operation);
}
