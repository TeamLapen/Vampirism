package de.teamlapen.vampirism.api.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Implemented by any crossbow.
 * Used for crossbow enchantments
 */
@SuppressWarnings("removal")
public interface IHunterCrossbow extends IVampirismCrossbow {

    /**
     * gets the required charge duration for the given crossbow
     *
     * @param crossbow itemstack of a {@link IHunterCrossbow}
     * @return duration in ticks
     */
    int getChargeDurationMod(ItemStack crossbow);

    /**
     * @param crossbow the crossbow
     * @return true if the crossbow can select ammunition
     */
    boolean canSelectAmmunition(ItemStack crossbow);

    /**
     * @return the currently selected ammunition or {@link java.util.Optional#empty()} if no ammunition is selected
     */
    Optional<Item> getAmmunition(ItemStack crossbow);

    /**
     * sets the selected ammunition for the crossbow
     *
     * @param crossbow the crossbow
     * @param ammo     the ammunition item
     */
    void setAmmunition(ItemStack crossbow, Item ammo);

    /**
     * gets a predicate to test all projectiles supported by the crossbow
     *
     * @param crossbow the crossbow
     * @return a predicate that returns true if the given itemstack is a supported projectile
     */
    Predicate<ItemStack> getSupportedProjectiles(ItemStack crossbow);
}
