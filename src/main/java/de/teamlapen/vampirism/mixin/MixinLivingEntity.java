package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    @Deprecated
    protected MixinLivingEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Shadow
    public abstract boolean addEffect(EffectInstance effectInstanceIn);

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void handleTotemOfUndying(DamageSource damageSourceIn, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && Helper.isVampire(this)) {
            this.addEffect(new EffectInstance(ModEffects.FIRE_PROTECTION.get(), 800, 5));
            this.addEffect(new EffectInstance(ModEffects.SUNSCREEN.get(), 800, 4));
        }
    }
}
