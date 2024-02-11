package de.teamlapen.vampirism.api.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Implemented by any crossbow.
 * Used for crossbow enchantments
 */
public interface IVampirismCrossbow extends ItemLike {

    /**
     * modded variant of {@link net.minecraft.world.item.CrossbowItem#performShooting(net.minecraft.world.level.Level, net.minecraft.world.entity.LivingEntity, net.minecraft.world.InteractionHand, net.minecraft.world.item.ItemStack, float, float)} that is not static
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
     * sets the selected ammunition for the crossbow
     *
     * @param crossbow the crossbow
     * @param ammo     the registry name if the ammunition item
     */
    void setAmmunition(ItemStack crossbow, ResourceLocation ammo);

    /**
     * gets a predicate to test all projectiles supported by the crossbow
     *
     * @param crossbow the crossbow
     * @return a predicate that returns true if the given itemstack is a supported projectile
     */
    Predicate<ItemStack> getSupportedProjectiles(ItemStack crossbow);
}
