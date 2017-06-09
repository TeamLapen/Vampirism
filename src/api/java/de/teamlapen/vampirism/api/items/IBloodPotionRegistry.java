package de.teamlapen.vampirism.api.items;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
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
    String CATEGORY_OTHERS = "o";

    /**
     * Special other effects
     */
    String CATEGORY_SPECIAL_OTHERS = "so";
    /**
     * Normal body boosts like speed 1
     */
    String CATEGORY_NORMAL_BODY_BOOSTS = "nbb";
    /**
     * Special  body boosts like speed 3
     */
    String CATEGORY_SPECIAL_BODY_BOOSTS = "sbb";

    /**
     * Normal  vampire skills like night vision
     */
    String CATEGORY_NORMAL_VAMPIRE_SKILLS = "nvs";
    /**
     * Special vampire skills like disguise as vampire
     */
    String CATEGORY_SPECIAL_VAMPIRE_SKILL = "svs";

    /**
     * Retrieves the potion effect for the given id if registered
     */
    @Nullable
    IBloodPotionEffect getEffectFromId(@Nonnull String id);

    /**
     * Get a localized multiline description for the given item's category.
     * Empty if no category belongs to that item
     */
    @Nonnull
    List<String> getLocCategoryDescForItem(@Nonnull ItemStack item);

    /**
     * Gets or creates a new category using given id and isBad.
     * Expects a list of items belonging to this category.
     * Add items or blocks if meta/nbt should be ignored or itemstacks if meta/nbt is relevant
     *
     * @param id        Identifier which can be used by different mods to modify the same category. The id will be unique for good categories as well as for bad categories
     * @param unlocDesc Only set if newly created. Can be null
     * @return The created category
     */
    IBloodPotionCategory getOrCreateCategory(@Nonnull String id, boolean isBad, @Nullable String unlocDesc);

    /**
     * Retrieve a random effect (under consideration  of the given item)
     *
     * @param bad If the effect should be bad
     */
    @Nonnull
    IBloodPotionEffect getRandomEffect(@Nullable ItemStack item, boolean bad, Random rng);

    /**
     * Register a new potion effect.
     *
     * @param id                 Unique id. Throws IllegalArgumentException if not
     * @param category           The effects category. Has to be registered. Throws IllegalArgumentException if not
     * @param isBad              if the effect ist bad
     * @param potion             The actual potion represented by this effect
     * @param weight             A weight value
     * @param propertyRandomizer randomizer for duration and amplifier
     * @return The created wrapper
     */
    IBloodPotionEffect registerPotionEffect(String id, IBloodPotionCategory category, boolean isBad, Potion potion, int weight, IBloodPotionPropertyRandomizer propertyRandomizer);

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
