package de.teamlapen.vampirism.api.items;

import com.google.errorprone.annotations.ForOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface ICrossbow extends ItemLike {


    /**
     * performs the actual shooting of the crossbow
     * @implNote This does not check if the crossbow is charged and does not the set . This should be done before calling this method
     */
    boolean performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle);

    /**
     * gets the required charge duration for the given crossbow
     *
     * @param crossbow itemstack of a {@link de.teamlapen.vampirism.api.items.ICrossbow}
     * @return duration in ticks
     */
    int getChargeDuration(ItemStack crossbow);

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

    /**
     * checks if the crossbow is charged
     * @param stack the stack of the crossbow
     * @implSpec This should call {@link CrossbowItem#isCharged(ItemStack)} or check against the same compound tag
     */
    boolean isCharged(ItemStack stack);

    /**
     * sets that charged flag of the crossbow
     * @param stack the stack of the crossbow
     * @param charged the new charged state
     * @implSpec This should call {@link CrossbowItem#setCharged(net.minecraft.world.item.ItemStack, boolean)} or modify the same compound tag
     */
    void setCharged(ItemStack stack, boolean charged);

    /**
     * Get all projectiles that are currently loaded into the crossbow
     * @param stack the stack of the crossbow
     * @return a list of all charged projectiles
     * @implSpec This should call {@link CrossbowItem#getChargedProjectiles(ItemStack)} or read the same compound tag
     */
    @SuppressWarnings("JavadocReference")
    List<ItemStack> getChargedProjectiles(ItemStack stack);

    /**
     * Get the pitches for each projectile when shooting
     */
    float[] getShotPitches(RandomSource pRandom);
}
