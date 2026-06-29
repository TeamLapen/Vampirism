package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.QuarrelProperties;
import de.teamlapen.vampirism.common.core.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;

public class GarlicBehavior extends QuarrelBehavior {

    public GarlicBehavior() {
        super(QuarrelProperties.of(0xFFFFFFFF).baseDamage(1).effectDescription(Component.translatable("tooltip.vampirism.quarrel_garlic")).build());
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        entity.addEffect(new MobEffectInstance(ModEffects.GARLIC, 100, 1));
    }
}
