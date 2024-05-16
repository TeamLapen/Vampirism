package de.teamlapen.vampirism.api.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * use {@link IHunterCrossbow}
 */
@Deprecated(forRemoval = true)
public interface IVampirismCrossbow extends ItemLike {

    int getChargeDurationMod(ItemStack crossbow);

    boolean canSelectAmmunition(ItemStack crossbow);

    Optional<Item> getAmmunition(ItemStack crossbow);

    void setAmmunition(ItemStack crossbow, Item ammo);

    Predicate<ItemStack> getSupportedProjectiles(ItemStack crossbow);
}
