package de.teamlapen.vampirism.mixin;

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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin extends Entity {

    @Shadow private Potion potion;

    @Unique
    private LivingEntity tick_local_entityLiving;

    @Deprecated
    private AreaEffectCloudMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyVariable(method = "tick", at = @At(value = "STORE", ordinal = 1))
    private MobEffectInstance l(MobEffectInstance e) {
        if (this.potion instanceof VampirismPotion.HunterPotion && Helper.isVampire(tick_local_entityLiving)) {
            return VampirismPoisonEffect.createEffectCloudEffect();
        } else {
            return e;
        }
    }



    @ModifyVariable(method = "tick", at = @At(value = "STORE", ordinal = 0))
    private LivingEntity l(LivingEntity e) {
        return tick_local_entityLiving = e;
    }
}
