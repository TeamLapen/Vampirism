package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.api.entity.effect.EffectWithNoCounter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MixinMobEffectInstance implements EffectInstanceWithSource {

    @Inject(method = "loadSpecifiedEffect(Lnet/minecraft/world/effect/MobEffect;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/effect/MobEffectInstance;", at = @At("RETURN"))
    private static void readInternal_vampirism(MobEffect effect, @NotNull CompoundTag nbt, @NotNull CallbackInfoReturnable<MobEffectInstance> cir) {
        if (nbt.contains("source")) {
            ((EffectInstanceWithSource) cir.getReturnValue()).setSource(new ResourceLocation(nbt.getString("source")));
        }
    }

    @Shadow
    int duration;
    @Shadow
    @Nullable
    private MobEffectInstance hiddenEffect;
    @Shadow @Final private MobEffect effect;
    private @Nullable ResourceLocation source;

    @Override
    @Nullable
    public MobEffectInstance getHiddenEffect() {
        return this.hiddenEffect;
    }

    @Override
    @Nullable
    public ResourceLocation getSource() {
        return this.source;
    }

    @Override
    public void setSource(@Nullable ResourceLocation source) {
        this.source = source;
    }

    @Override
    public boolean hasSource() {
        return this.source != null;
    }

    @Override
    public void removeEffect() {
        this.duration = 1;
    }

    /*@Inject(method = "isNoCounter", at = @At("HEAD"), cancellable = true) //TODO 1.19 readd
    private void isNoCounter(CallbackInfoReturnable<Boolean> cir) {
        if (this.effect instanceof EffectWithNoCounter) {
            cir.setReturnValue(true);
        }
    }*/

    @Inject(method = "update(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At(value = "JUMP", ordinal = 2))
    private void copySource(@NotNull MobEffectInstance other, CallbackInfoReturnable<Boolean> cir) {
        this.source = ((EffectInstanceWithSource) other).getSource();
    }

    @Inject(method = "setDetailsFrom(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
    private void copySource1(@NotNull MobEffectInstance other, CallbackInfo ci) {
        this.source = ((EffectInstanceWithSource) other).getSource();
    }

    @Inject(method = "writeDetailsTo(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void writeInternal_vampirism(@NotNull CompoundTag nbt, CallbackInfo ci) {
        if (source != null) {
            nbt.putString("source", source.toString());
        }
    }
}
