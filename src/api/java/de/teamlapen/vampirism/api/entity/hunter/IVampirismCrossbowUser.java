package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

/**
 * must be extended by {@link net.minecraft.entity.LivingEntity}
 */
public interface IVampirismCrossbowUser extends ICrossbowUser {

    boolean isHoldingCrossbow();

    boolean isChargingCrossbow();

    default boolean canUseCrossbow(ItemStack stack){
        return true;
    }

    /**
     * current crossbow arm pose to use for rendering
     */
    default ArmPose getArmPose() {
        if (isHoldingCrossbow()) {
            if (isChargingCrossbow()) {
                return ArmPose.CROSSBOW_CHARGE;
            }
            Hand hand = ProjectileHelper.getWeaponHoldingHand(((LivingEntity) this), IVampirismCrossbow.class::isInstance);
            ItemStack itemstack = ((LivingEntity) this).getItemInHand(hand);
            if (itemstack.getItem() instanceof IVampirismCrossbow && CrossbowItem.isCharged(itemstack)) {
                return ArmPose.CROSSBOW_HOLD;
            }
        }
        return ArmPose.NEUTRAL;
    }

    @Override
    default void performCrossbowAttack(@Nonnull LivingEntity entity, float speed) {
        Hand hand = ProjectileHelper.getWeaponHoldingHand(entity, IVampirismCrossbow.class::isInstance);
        ItemStack itemstack = entity.getItemInHand(hand);
        if (itemstack.getItem() instanceof IVampirismCrossbow) {
            if (CrossbowItem.isCharged(itemstack)){
                ((IVampirismCrossbow) itemstack.getItem()).performShootingMod(entity.level, entity, hand, itemstack, speed, (float)(14 - entity.level.getDifficulty().getId() * 4));
                this.onCrossbowAttackPerformed();
            }
        }
    }

    enum ArmPose {
        NEUTRAL, CROSSBOW_HOLD, CROSSBOW_CHARGE;
    }
}
