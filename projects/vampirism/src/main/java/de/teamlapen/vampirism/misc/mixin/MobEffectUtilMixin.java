package de.teamlapen.vampirism.misc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.common.core.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectUtil.class)
public class MobEffectUtilMixin {

    @Inject(method = "formatDuration", at = @At(value = "HEAD"), cancellable = true)
    private static void vampirism$sanguinare(MobEffectInstance instance, float scale, float tickrate, CallbackInfoReturnable<Component> cir) {
        if (instance.is(ModEffects.SANGUINARE)) {
            cir.setReturnValue(Component.translatable("effect.vampirism.duration.unknown"));
        }
    }
}
