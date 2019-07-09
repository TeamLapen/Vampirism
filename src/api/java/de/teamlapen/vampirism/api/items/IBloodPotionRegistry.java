package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.ThreadSafeAPI;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Registry for blood potions.
 */
public interface IBloodPotionRegistry {

    /**
     * Other effects
     */
    ResourceLocation CATEGORY_OTHERS = new ResourceLocation("vampirism", "o");

    /**
     * Special other effects
     */
    ResourceLocation CATEGORY_SPECIAL_OTHERS = new ResourceLocation("vampirism", "so");
    /**
     * Normal body boosts like speed 1
     */
    ResourceLocation CATEGORY_NORMAL_BODY_BOOSTS = new ResourceLocation("vampirism", "nbb");
    /**
     * Special  body boosts like speed 3
     */
    ResourceLocation CATEGORY_SPECIAL_BODY_BOOSTS = new ResourceLocation("vampirism", "sbb");

    /**
     * Normal  vampire skills like night vision
     */
    ResourceLocation CATEGORY_NORMAL_VAMPIRE_SKILLS = new ResourceLocation("vampirism", "nvs");
    /**
     * Special vampire skills like disguise as vampire
     */
    ResourceLocation CATEGORY_SPECIAL_VAMPIRE_SKILL = new ResourceLocation("vampirism", "svs");

    /**
     * Retrieves the potion effect for the given id if registered
     */
    @Nullable
    IBloodPotionEffect getEffectFromId(@Nonnull ResourceLocation id);

    /**
     * Get a localized multiline description for the given item's category.
     * Empty if no category belongs to that item
     */
    @Nonnull
    List<String> getLocCategoryDescForItem(@Nonnull ItemStack item);


    /**
     * Retrieve a random effect (under consideration  of the given item)
     *
     * @param item may be EMPTY
     * @param bad  If the effect should be bad
     */
    @Nonnull
    IBloodPotionEffect getRandomEffect(@Nonnull ItemStack item, boolean bad, Random rng);

    /**
     * Add items to a category
     * Adds all items in the list. See {@link IBloodPotionCategory#addItem(Item)},{@link IBloodPotionCategory#addItem(Block)},{@link IBloodPotionCategory#addItemExact(ItemStack)}
     *
     * @param categoryId Id of the category. Non-existent ones will be created
     */
    @ThreadSafeAPI
    void addItemsToCategory(boolean bad, @Nonnull ResourceLocation categoryId, Item... items);

    /**
     * Register a new potion effect.
     *
     * @param id                 Unique id. Throws IllegalArgumentException if not
     * @param categoryId         Identifier of a blood potion category. Non-existent ones will be created
     * @param isBad              if the effect ist bad
     * @param potion             The actual potion represented by this effect
     * @param weight             A weight value
     * @param propertyRandomizer randomizer for duration and amplifier
     */
    @ThreadSafeAPI
    void registerPotionEffect(ResourceLocation id, ResourceLocation categoryId, boolean isBad, Effect potion, int weight, IBloodPotionPropertyRandomizer propertyRandomizer);

    class WeightedEffect extends WeightedRandom.Item {
        public final IBloodPotionEffect effect;

        public WeightedEffect(IBloodPotionEffect effect, int itemWeightIn) {
            super(itemWeightIn);
            this.effect = effect;
        }

        public WeightedEffect copy() {
            return new WeightedEffect(effect, itemWeight);
        }
    }


}
