package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.teamlapen.vampirism.effects.VampirismPoisonEffect;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin extends Entity {

    @Shadow private Potion potion;

    @Deprecated
    private AreaEffectCloudMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyVariable(method = "tick", at = @At(value = "STORE", ordinal = 1))
    private MobEffectInstance l(MobEffectInstance e, @Local(ordinal = 0) LivingEntity entity) {
        if (this.potion instanceof VampirismPotion.HunterPotion && Helper.isVampire(entity)) {
            return VampirismPoisonEffect.createEffectCloudEffect();
        } else {
            return e;
        }
    }

}
