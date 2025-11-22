package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    public abstract boolean addEffect(MobEffectInstance effectInstance);

    private LivingEntityMixin(@NotNull EntityType<? extends LivingEntity> type, @NotNull Level level) {
        super(type, level);
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "RETURN", ordinal = 1))
    private void handleTotemOfUndying(DamageSource damageSource, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && Helper.isVampire(this)) {
            this.addEffect(new MobEffectInstance(ModEffects.FIRE_PROTECTION, 800, 5));
            this.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN, 800, 4));
        }
    }
}
