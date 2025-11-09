package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.misc.extension.IEffectInstanceWithSource;
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IEffectInstanceWithSource {

    @Shadow
    private int duration;

    @Shadow
    @Nullable
    private MobEffectInstance hiddenEffect;

    @Unique
    private final Set<ResourceLocation> vampirism$properties = new HashSet<>();

    @Override
    @Nullable
    public MobEffectInstance vampirism$getHiddenEffect() {
        return this.hiddenEffect;
    }

    public Set<ResourceLocation> vampirism$getProperties() {
        return this.vampirism$properties;
    }

    @Override
    public boolean vampirism$hasProperty(@Nullable ResourceLocation source) {
        return this.vampirism$properties.contains(source);
    }

    @Override
    public void vampirism$setProperties(Collection<ResourceLocation> sources) {
        this.vampirism$properties.clear();
        this.vampirism$properties.addAll(sources);
    }

    @Override
    public void vampirism$addProperty(@Nullable ResourceLocation source) {
        if (source == null) return;
        this.vampirism$properties.add(source);
    }

    @Override
    public boolean vampirism$hasProperties() {
        return !this.vampirism$properties.isEmpty();
    }

    @Override
    public void vampirism$removeEffect() {
        this.duration = 1;
    }

    @Inject(method = "update(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At(value = "JUMP", ordinal = 2))
    private void copySource(@NotNull MobEffectInstance other, CallbackInfoReturnable<Boolean> cir) {
        this.vampirism$properties.clear();
        this.vampirism$properties.addAll(((IEffectInstanceWithSource) other).vampirism$getProperties());
    }

    @Inject(method = "setDetailsFrom(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
    private void copySource1(@NotNull MobEffectInstance other, CallbackInfo ci) {
        this.vampirism$properties.clear();
        this.vampirism$properties.addAll(((IEffectInstanceWithSource) other).vampirism$getProperties());
    }
}
