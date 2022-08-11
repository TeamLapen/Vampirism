package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Implemented by any crossbow.
 * Used for crossbow enchantments
 */
public interface IVampirismCrossbow {

    /**
     * modded variant of {@link net.minecraft.item.CrossbowItem#performShooting(net.minecraft.world.World, net.minecraft.entity.LivingEntity, net.minecraft.util.Hand, net.minecraft.item.ItemStack, float, float)} that is not static
     */
    void performShootingMod(World level, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float angle);
}
