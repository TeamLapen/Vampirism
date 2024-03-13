package de.teamlapen.vampirism.api.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * @deprecated use {@link de.teamlapen.vampirism.api.items.ICrossbow} or {@link de.teamlapen.vampirism.api.items.IHunterCrossbow} instead
 */
@Deprecated(forRemoval = true)
public interface IVampirismCrossbow extends ICrossbow {

    boolean performShootingMod(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle);

    @Override
    default boolean performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float inaccuracy, float angle) {
        return performShootingMod(level, shooter, hand, stack, speed, angle);
    }

    int getChargeDurationMod(ItemStack crossbow);

    @Override
    default int getChargeDuration(ItemStack crossbow) {
        return getChargeDurationMod(crossbow);
    }
}
