package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.items.ICrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * must be extended by {@link net.minecraft.world.entity.LivingEntity}
 */
public interface IVampirismCrossbowUser extends CrossbowAttackMob {

    boolean isHoldingCrossbow();

    boolean isChargingCrossbow();

    default boolean canUseCrossbow(ItemStack stack){
        return true;
    }

    @Override
    default void performCrossbowAttack(@Nonnull LivingEntity entity, float speed) {
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(entity, ICrossbow.class::isInstance);
        ItemStack itemstack = entity.getItemInHand(hand);
        if (itemstack.getItem() instanceof ICrossbow crossbow) {
            if (crossbow.isCharged(itemstack)){
                crossbow.performShooting(entity.level(), entity, hand, itemstack, speed, (float) (14 - entity.level().getDifficulty().getId() * 4));
                this.onCrossbowAttackPerformed();
            }
        }
    }

    enum ArmPose {
        NEUTRAL, CROSSBOW_HOLD, CROSSBOW_CHARGE
    }
}
