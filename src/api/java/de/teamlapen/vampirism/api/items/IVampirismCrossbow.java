package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Implemented by any crossbow.
 * Used for crossbow enchanments
 */
public interface IVampirismCrossbow {

    void performShootingMod(World level, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float angle);
}
