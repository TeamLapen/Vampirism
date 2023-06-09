package de.teamlapen.vampirism.api.entity.hunter;

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

    /**
     * current crossbow arm pose to use for rendering
     */
    default ArmPose getArmPose() {
        if (isHoldingCrossbow()) {
            if (isChargingCrossbow()) {
                return ArmPose.CROSSBOW_CHARGE;
            }
            InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(((LivingEntity) this), IVampirismCrossbow.class::isInstance);
            ItemStack itemstack = ((LivingEntity) this).getItemInHand(hand);
            if (itemstack.getItem() instanceof IVampirismCrossbow && CrossbowItem.isCharged(itemstack)) {
                return ArmPose.CROSSBOW_HOLD;
            }
        }
        return ArmPose.NEUTRAL;
    }

    @Override
    default void performCrossbowAttack(@Nonnull LivingEntity entity, float speed) {
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(entity, IVampirismCrossbow.class::isInstance);
        ItemStack itemstack = entity.getItemInHand(hand);
        if (itemstack.getItem() instanceof IVampirismCrossbow) {
            if (CrossbowItem.isCharged(itemstack)){
                ((IVampirismCrossbow) itemstack.getItem()).performShootingMod(entity.level(), entity, hand, itemstack, speed, (float) (14 - entity.level().getDifficulty().getId() * 4));
                this.onCrossbowAttackPerformed();
            }
        }
    }

    enum ArmPose {
        NEUTRAL, CROSSBOW_HOLD, CROSSBOW_CHARGE;
    }
}
