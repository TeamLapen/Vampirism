package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.QuarrelProperties;
import de.teamlapen.vampirism.common.core.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;

public class BleedingBehavior extends QuarrelBehavior {

    public BleedingBehavior() {
        super(QuarrelProperties.of(0xFFAA0000).baseDamage(0.5f).effectDescription(Component.translatable("tooltip.vampirism.quarrel_bleeding")).build());
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity hitEntity, AbstractArrow arrowEntity, Entity shootingEntity) {
        hitEntity.addEffect(new MobEffectInstance(ModEffects.BLEEDING, 40, 0, false, false));
    }
}
