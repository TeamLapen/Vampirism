package de.teamlapen.vampirism.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.teamlapen.vampirism.common.effects.VampirismPoisonMobEffect;
import de.teamlapen.vampirism.common.potions.BasePotion;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin extends Entity {

    @Shadow
    private PotionContents potionContents;

    @Deprecated
    private AreaEffectCloudMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyVariable(method = "serverTick", at = @At(value = "STORE", ordinal = 0))
    private MobEffectInstance replaceEffectForVampires(MobEffectInstance effectInstance, @Local(ordinal = 0) LivingEntity entity) {
        return this.potionContents.potion()
                .map(Holder::value)
                .filter(BasePotion.HunterPotion.class::isInstance)
                .filter(potion -> Helper.isVampire(entity))
                .map(s -> VampirismPoisonMobEffect.createEffectCloudEffect())
                .orElse(effectInstance);
    }
}
