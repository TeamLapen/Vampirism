package de.teamlapen.vampirism.api.items;

import com.google.errorprone.annotations.ForOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * use {@link IHunterCrossbow}
 */
@Deprecated(forRemoval = true)
public interface IVampirismCrossbow extends ItemLike {

    int getChargeDurationMod(ItemStack crossbow, Level level);

    boolean canSelectAmmunition(ItemStack crossbow);

    Optional<Item> getAmmunition(ItemStack crossbow);

    void setAmmunition(ItemStack crossbow, Item ammo);

    Predicate<ItemStack> getSupportedProjectiles(ItemStack crossbow);
}
