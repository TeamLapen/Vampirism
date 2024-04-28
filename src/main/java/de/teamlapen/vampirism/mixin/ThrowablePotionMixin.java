package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.effects.VampirismPoisonEffect;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrownPotion.class)
public abstract class ThrowablePotionMixin extends ThrowableItemProjectile {

    @Unique
    private LivingEntity tick_local_entityLiving;

    @Deprecated
    private ThrowablePotionMixin(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyVariable(method = "applySplash", at = @At(value = "STORE", ordinal = 0))
    private LivingEntity l(LivingEntity e) {
        return tick_local_entityLiving = e;
    }

    @ModifyVariable(method = "applySplash", at = @At(value = "STORE", ordinal = 0))
    private MobEffectInstance l(MobEffectInstance e) {
        if (this.getItem().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().map(s -> s.value() instanceof VampirismPotion.HunterPotion).orElse(false) && Helper.isVampire(tick_local_entityLiving)) {
            return VampirismPoisonEffect.createThrowableEffect();
        } else {
            return e;
        }
    }
}
