package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    private MixinLivingEntity(@NotNull EntityType<? extends LivingEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    @Shadow
    public abstract boolean addEffect(MobEffectInstance effectInstanceIn);

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "RETURN", ordinal = 1))
    private void handleTotemOfUndying(DamageSource damageSourceIn, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && Helper.isVampire(this)) {
            this.addEffect(new MobEffectInstance(ModEffects.FIRE_PROTECTION, 800, 5));
            this.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN, 800, 4));
        }
    }

//    @ModifyReturnValue(method = "canFreeze", at = @At("RETURN"))
//    private boolean vampireFreeze(boolean original) {
//        if (original && Helper.isVampire(this)) {
//            return false;
//        }
//        return original;
//    }

}
