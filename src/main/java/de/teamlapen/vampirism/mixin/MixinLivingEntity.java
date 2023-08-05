package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    protected MixinLivingEntity(@NotNull EntityType<? extends LivingEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    @Shadow
    public abstract boolean addEffect(MobEffectInstance effectInstanceIn);

    @Shadow
    public abstract ItemStack getMainHandItem(); // Needed in subclass


    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"))
    private void handleTotemOfUndying(DamageSource damageSourceIn, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && Helper.isVampire(this)) {
            this.addEffect(new MobEffectInstance(ModEffects.FIRE_PROTECTION.get(), 800, 5));
            this.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN.get(), 800, 4));
        }
    }

    protected MobType vampirism$getMobType() {
        return null;
    }

    @Inject(method = "getMobType", at = @At("HEAD"), cancellable = true)
    private void handleGetMobType(CallbackInfoReturnable<MobType> cir) {
        MobType t = vampirism$getMobType();
        if (t != null) {
            cir.setReturnValue(t);
            cir.cancel();
        }
    }
}
