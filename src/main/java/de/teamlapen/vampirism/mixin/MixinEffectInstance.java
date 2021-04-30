package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EffectInstance.class)
public class MixinEffectInstance implements EffectInstanceWithSource {

    @Shadow private int duration;
    @Shadow @Nullable private EffectInstance hiddenEffects;
    private ResourceLocation source;

    @Override
    public void setSource(@Nullable ResourceLocation source) {
        this.source = source;
    }

    @Override
    @Nullable
    public ResourceLocation getSource() {
        return this.source;
    }

    @Override
    public boolean hasSource() {
        return this.source != null;
    }

    @Override
    public void removeEffect() {
        this.duration = 1;
    }

    @Override
    @Nullable
    public EffectInstance getHiddenEffect() {
        return this.hiddenEffects;
    }

    @Inject(method = "func_230117_a_(Lnet/minecraft/potion/EffectInstance;)V", at = @At("TAIL"))
    private void copySource1(EffectInstance other, CallbackInfo ci){
        this.source = ((EffectInstanceWithSource) other).getSource();
    }

    @Inject(method = "writeInternal(Lnet/minecraft/nbt/CompoundNBT;)V", at = @At("TAIL"))
    private void writeInternale_vampirism(CompoundNBT nbt, CallbackInfo ci){
        if (source != null) {
            nbt.putString("source", source.toString());
        }
    }

    @Inject(method = "readInternal(Lnet/minecraft/potion/Effect;Lnet/minecraft/nbt/CompoundNBT;)Lnet/minecraft/potion/EffectInstance;", at = @At("RETURN"))
    private static void readInternal_vampirism(Effect effect, CompoundNBT nbt, CallbackInfoReturnable<EffectInstance> cir){
        if (nbt.contains("source")) {
            ((EffectInstanceWithSource) cir.getReturnValue()).setSource(new ResourceLocation(nbt.getString("source")));
        }
    }

    @Inject(method = "combine(Lnet/minecraft/potion/EffectInstance;)Z", at = @At(value = "JUMP", ordinal = 2))
    private void copySource(EffectInstance other, CallbackInfoReturnable<Boolean> cir){
        this.source = ((EffectInstanceWithSource) other).getSource();
    }
}
