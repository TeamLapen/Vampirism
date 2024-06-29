package de.teamlapen.vampirism.api.entity.hunter;

import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;

/**
 * must be extended by {@link net.minecraft.world.entity.LivingEntity}
 */
public interface IVampirismCrossbowUser extends CrossbowAttackMob, RangedAttackMob {

    boolean isHoldingCrossbow();

    boolean isChargingCrossbow();

    default boolean canUseCrossbow(ItemStack stack) {
        return true;
    }

    enum ArmPose {
        NEUTRAL, CROSSBOW_HOLD, CROSSBOW_CHARGE
    }
}
