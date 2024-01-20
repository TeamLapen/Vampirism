package de.teamlapen.vampirism.mixin.client;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EffectRenderingInventoryScreen.class)
public interface EffectRenderingInventoryScreenAccessor {

    @Invoker("getEffectName(Lnet/minecraft/world/effect/MobEffectInstance;)Lnet/minecraft/network/chat/Component;")
    Component getEffectName(MobEffectInstance pEffect);
}
