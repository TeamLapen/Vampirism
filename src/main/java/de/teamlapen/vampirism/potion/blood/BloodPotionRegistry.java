package de.teamlapen.vampirism.potion.blood;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.items.IBloodPotionCategory;
import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionPropertyRandomizer;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BloodPotionRegistry implements IBloodPotionRegistry {

    private final List<BloodPotionCategory> categoriesGood = Lists.newArrayList();
    private final List<BloodPotionCategory> categoriesBad = Lists.newArrayList();
    private final Map<ResourceLocation, IBloodPotionEffect> allEffects = Maps.newHashMap();

    private final Queue<Triple<ResourceLocation, Boolean, Object[]>> categoryItemsQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Triple<ResourceLocation, Boolean, BloodPotionEffect>> potionsEffectsQueue = new ConcurrentLinkedQueue<>();




    @Nullable
    @Override
    public IBloodPotionEffect getEffectFromId(@Nonnull String id) {
        return allEffects.get(id);
    }

    @Nonnull
    @Override
    public List<String> getLocCategoryDescForItem(@Nonnull ItemStack item) {
        assert !item.isEmpty();
        List<IBloodPotionCategory> categories = Lists.newLinkedList();
        categories.addAll(categoriesBad);
        categories.addAll(categoriesGood);
        List<String> desc = Lists.newArrayList();
        for (IBloodPotionCategory category : categories) {
            if (category.containsItem(item)) {
                desc.add(UtilLib.translate(category.getDescTranslationKey()));

            }
        }
        return desc;
    }

    @ThreadSafeAPI
    @Override
    public void addItemsToCategory(boolean bad, @Nonnull ResourceLocation categoryId, Objects... items) {
        categoryItemsQueue.add(Triple.of(categoryId, bad, items));
    }

    @Nonnull
    @Override
    public IBloodPotionEffect getRandomEffect(@Nonnull ItemStack item, boolean bad, Random rnd) {
        List<WeightedEffect> effects = Lists.newArrayList();
        List<BloodPotionCategory> categories = bad ? categoriesBad : categoriesGood;

        for (BloodPotionCategory category : categories) {
            if (!item.isEmpty() && category.containsItem(item)) {
                for (WeightedEffect effect : category.getEffectsCopy()) {
                    effect.itemWeight *= 5;
                    effects.add(effect);
                }
            } else {
                effects.addAll(category.getEffectsCopy());
            }

        }
        return WeightedRandom.getRandomItem(rnd, effects).effect;
    }

    public void finish() {
        for (Triple<ResourceLocation, Boolean, Object[]> t : categoryItemsQueue) {
            IBloodPotionCategory cat = getOrCreateCategory(t.getLeft(), t.getMiddle());
            cat.addItems(t.getRight());
        }

        for (Triple<ResourceLocation, Boolean, BloodPotionEffect> t : potionsEffectsQueue) {
            IBloodPotionCategory cat = getOrCreateCategory(t.getLeft(), t.getMiddle());
            BloodPotionEffect effect = t.getRight();
            if (allEffects.containsKey(effect.getId())) {
                throw new IllegalArgumentException("Blood Potion Effect with id " + effect.getId() + " is already registered: " + allEffects.get(effect.getId()));
            }
            allEffects.put(effect.getId(), effect);
            ((BloodPotionCategory) cat).addEffect(effect, effect.getWeight());
        }
        categoryItemsQueue.clear();
        potionsEffectsQueue.clear();
    }

    @ThreadSafeAPI
    @Override
    public void registerPotionEffect(ResourceLocation id, ResourceLocation categoryId, boolean isBad, Potion potion, int weight, IBloodPotionPropertyRandomizer propertyRandomizer) {
        BloodPotionEffect effect = new BloodPotionEffect(id, potion, isBad, weight, propertyRandomizer);
        potionsEffectsQueue.add(Triple.of(categoryId, isBad, effect));
    }

    /**
     * Gets or creates a new category using given id and isBad.
     * Expects a list of items belonging to this category.
     * Add items or blocks if meta/nbt should be ignored or itemstacks if meta/nbt is relevant
     *
     * @param id Identifier which can be used by different mods to modify the same category. The id will be unique for good categories as well as for bad categories
     * @return The created category
     */
    private IBloodPotionCategory getOrCreateCategory(@Nonnull ResourceLocation id, boolean isBad) {
        List<BloodPotionCategory> categories = isBad ? categoriesBad : categoriesGood;
        for (BloodPotionCategory category : categories) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        BloodPotionCategory category = new BloodPotionCategory(id);
        categories.add(category);
        return category;

    }
}
