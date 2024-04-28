package de.teamlapen.vampirism.mixin.client.accessor;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EffectRenderingInventoryScreen.class)
public interface EffectRenderingInventoryScreenAccessor {

    @Invoker("getEffectName")
    Component invoke_getEffectName(MobEffectInstance pEffect);
}
