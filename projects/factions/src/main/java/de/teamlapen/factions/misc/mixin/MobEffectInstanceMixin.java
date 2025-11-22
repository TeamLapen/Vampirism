package de.teamlapen.factions.misc.mixin;

import de.teamlapen.factions.misc.extensions.IEffectInstanceWithSource;
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
    private final Set<ResourceLocation> factions$properties = new HashSet<>();

    @Override
    @Nullable
    public MobEffectInstance factions$getHiddenEffect() {
        return this.hiddenEffect;
    }

    public Set<ResourceLocation> factions$getProperties() {
        return this.factions$properties;
    }

    @Override
    public boolean factions$hasProperty(@Nullable ResourceLocation source) {
        return this.factions$properties.contains(source);
    }

    @Override
    public void factions$setProperties(Collection<ResourceLocation> sources) {
        this.factions$properties.clear();
        this.factions$properties.addAll(sources);
    }

    @Override
    public void factions$addProperty(@Nullable ResourceLocation source) {
        if (source == null) return;
        this.factions$properties.add(source);
    }

    @Override
    public boolean factions$hasProperties() {
        return !this.factions$properties.isEmpty();
    }

    @Override
    public void factions$removeEffect() {
        this.duration = 1;
    }

    @Inject(method = "update(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At(value = "JUMP", ordinal = 2))
    private void copySource(@NotNull MobEffectInstance other, CallbackInfoReturnable<Boolean> cir) {
        this.factions$properties.clear();
        this.factions$properties.addAll(((IEffectInstanceWithSource) other).factions$getProperties());
    }

    @Inject(method = "setDetailsFrom(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
    private void copySource1(@NotNull MobEffectInstance other, CallbackInfo ci) {
        this.factions$properties.clear();
        this.factions$properties.addAll(((IEffectInstanceWithSource) other).factions$getProperties());
    }
}
