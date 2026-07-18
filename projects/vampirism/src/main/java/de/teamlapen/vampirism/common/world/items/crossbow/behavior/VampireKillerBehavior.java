package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.world.items.QuarrelProperties;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.items.StakeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;

public class VampireKillerBehavior extends QuarrelBehavior {

    public VampireKillerBehavior() {
        super(QuarrelProperties.of(0xFF7A0073).baseDamage(0.5f).effectDescription(Component.translatable("tooltip.vampirism.quarrel_vampire_killer")).build());
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        if (entity.level() instanceof ServerLevel level && (entity instanceof IVampireMob || (entity instanceof Player player && Helper.isVampire(player)))) {
            if (shootingEntity instanceof LivingEntity shooter && StakeItem.canKillInstantly(entity, shooter)) {
                DamageHandler.hurtModded(level, entity, s -> s.stake(shooter), 10000F);
            }
        }
    }
}
