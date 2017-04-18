package de.teamlapen.vampirism.potion.blood;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.items.IBloodPotionCategory;
import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionPropertyRandomizer;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BloodPotionRegistry implements IBloodPotionRegistry {

    private final List<BloodPotionCategory> categoriesGood = Lists.newArrayList();
    private final List<BloodPotionCategory> categoriesBad = Lists.newArrayList();
    private final Map<String, IBloodPotionEffect> allEffects = Maps.newHashMap();

    @Nullable
    @Override
    public IBloodPotionEffect getEffectFromId(@Nonnull String id) {
        return allEffects.get(id);
    }

    @Nonnull
    @Override
    public List<String> getLocCategoryDescForItem(@Nonnull ItemStack item) {
        assert !ItemStackUtil.isEmpty(item);
        List<IBloodPotionCategory> categories = Lists.newLinkedList();
        categories.addAll(categoriesBad);
        categories.addAll(categoriesGood);
        List<String> desc = Lists.newArrayList();
        for (IBloodPotionCategory category : categories) {
            if (category.containsItem(item)) {
                desc.add(UtilLib.translate(category.getUnlocDescription()));

            }
        }
        return desc;
    }

    @Override
    public IBloodPotionCategory getOrCreateCategory(@Nonnull String id, boolean isBad, String unlocDesc) {
        List<BloodPotionCategory> categories = isBad ? categoriesBad : categoriesGood;
        for (BloodPotionCategory category : categories) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        BloodPotionCategory category = new BloodPotionCategory(id, unlocDesc);
        categories.add(category);
        return category;

    }

    @Nonnull
    @Override
    public IBloodPotionEffect getRandomEffect(@Nonnull ItemStack item, boolean bad, Random rnd) {
        List<WeightedEffect> effects = Lists.newArrayList();
        List<BloodPotionCategory> categories = bad ? categoriesBad : categoriesGood;

        for (BloodPotionCategory category : categories) {
            if (!ItemStackUtil.isEmpty(item) && category.containsItem(item)) {
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

    @Override
    public IBloodPotionEffect registerPotionEffect(String id, IBloodPotionCategory category, boolean isBad, Potion potion, int weight, IBloodPotionPropertyRandomizer randomizer) {
        if (allEffects.containsKey(id)) {
            throw new IllegalArgumentException("Blood Potion Effect with id " + id + " is already registered: " + allEffects.get(id));
        }
        if (!categoriesGood.contains(category) && !categoriesBad.contains(category)) {
            throw new IllegalArgumentException("Category " + category + " is not registered");
        }
        BloodPotionEffect effect = new BloodPotionEffect(id, potion, isBad, weight, randomizer);
        allEffects.put(id, effect);
        ((BloodPotionCategory) category).addEffect(effect, weight);
        return effect;
    }
}
