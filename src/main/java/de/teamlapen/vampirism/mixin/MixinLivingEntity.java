package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    @Deprecated
    protected MixinLivingEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Shadow
    public abstract boolean addEffect(MobEffectInstance effectInstanceIn);

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void handleTotemOfUndying(DamageSource damageSourceIn, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && Helper.isVampire(this)) {
            this.addEffect(new MobEffectInstance(ModEffects.FIRE_PROTECTION.get(), 800, 5));
            this.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN.get(), 800, 4));
        }
    }
}
