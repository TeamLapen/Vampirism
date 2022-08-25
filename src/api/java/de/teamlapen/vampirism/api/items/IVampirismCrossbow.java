package de.teamlapen.vampirism.api.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

/**
 * Implemented by any crossbow.
 * Used for crossbow enchantments
 */
public interface IVampirismCrossbow extends ItemLike{

    /**
     * modded variant of {@link net.minecraft.item.CrossbowItem#performShooting(net.minecraft.world.World, net.minecraft.entity.LivingEntity, net.minecraft.util.Hand, net.minecraft.item.ItemStack, float, float)} that is not static
     *
     * @return true if the crossbow is empty
     */
    boolean performShootingMod(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle);

    /**
     * gets the required charge duration for the given crossbow
     *
     * @param crossbow itemstack of a {@link IVampirismCrossbow}
     * @return duration in ticks
     */
    int getChargeDurationMod(ItemStack crossbow);
}
