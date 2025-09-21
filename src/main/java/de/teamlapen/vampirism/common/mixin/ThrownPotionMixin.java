package de.teamlapen.vampirism.common.mixin;

import de.teamlapen.vampirism.common.effects.VampirismPoisonMobEffect;
import de.teamlapen.vampirism.common.potions.BasePotion;
import de.teamlapen.vampirism.common.util.Helper;
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
public abstract class ThrownPotionMixin extends ThrowableItemProjectile {

    @Unique
    private LivingEntity vampirism$currentAffectedEntity;

    @Deprecated
    private ThrownPotionMixin(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyVariable(method = "applySplash", at = @At(value = "STORE", ordinal = 0))
    private LivingEntity setCurrentEntity(LivingEntity entity) {
        return vampirism$currentAffectedEntity = entity;
    }

    @ModifyVariable(method = "applySplash", at = @At(value = "STORE", ordinal = 0))
    private MobEffectInstance checkHunterPotions(MobEffectInstance effectInstance) {
        if (this.getItem().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().map(s -> s.value() instanceof BasePotion.HunterPotion).orElse(false) && Helper.isVampire(vampirism$currentAffectedEntity)) {
            return VampirismPoisonMobEffect.createThrowableEffect();
        } else {
            return effectInstance;
        }
    }
}
