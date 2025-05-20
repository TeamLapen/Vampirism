package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements EffectInstanceWithSource {

    @Shadow
    private int duration;

    @Shadow
    @Nullable
    private MobEffectInstance hiddenEffect;

    @Unique
    private @Nullable ResourceLocation vampirism$source;

    @Override
    @Nullable
    public MobEffectInstance vampirism$getHiddenEffect() {
        return this.hiddenEffect;
    }

    @Override
    @Nullable
    public ResourceLocation vampirism$getSource() {
        return this.vampirism$source;
    }

    @Override
    public void vampirism$setSource(@Nullable ResourceLocation source) {
        this.vampirism$source = source;
    }

    @Override
    public boolean vampirism$hasSource() {
        return this.vampirism$source != null;
    }

    @Override
    public void vampirism$removeEffect() {
        this.duration = 1;
    }

    @Inject(method = "update(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At(value = "JUMP", ordinal = 2))
    private void copySource(@NotNull MobEffectInstance other, CallbackInfoReturnable<Boolean> cir) {
        this.vampirism$source = ((EffectInstanceWithSource) other).vampirism$getSource();
    }

    @Inject(method = "setDetailsFrom(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
    private void copySource1(@NotNull MobEffectInstance other, CallbackInfo ci) {
        this.vampirism$source = ((EffectInstanceWithSource) other).vampirism$getSource();
    }
}
