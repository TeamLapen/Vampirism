package de.teamlapen.vampirism.inventory.recipes;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipe;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;

public class ExtendedReagentRecipes implements IExtendedBrewingRecipe {


    @Override
    public Optional<Triple<ItemStack, Integer, Integer>> getOutput(ItemStack bottleItem, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingRecipeRegistry.IExtendedBrewingCapabilities capabilities) {
        if (!bottleItem.isEmpty()) {
            Potion potion = PotionUtils.getPotionFromItem(bottleItem);
            Item item = bottleItem.getItem();
            for (IExtendedBrewingRecipeRegistry.MixPredicate mix : VampirismAPI.extendedBrewingRecipeRegistry().getConversionMixes()) {
                if (mix.input.get() == potion && mix.reagent1.test(ingredient) && ingredient.getCount() >= mix.reagent1Count && mix.reagent2.test(extraIngredient) && extraIngredient.getCount() >= mix.reagent2Count && mix.condition.test(capabilities)) {
                    return Optional.of(Triple.of(PotionUtils.addPotionToItemStack(new ItemStack(item), mix.output.get()), mix.reagent1Count, mix.reagent2Count));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isExtraIngredient(ItemStack extraIngredient) {
        for (IExtendedBrewingRecipeRegistry.MixPredicate mix : VampirismAPI.extendedBrewingRecipeRegistry().getConversionMixes()) {
            if (mix.reagent2.test(extraIngredient)) return true;
        }
        return false;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        for (IExtendedBrewingRecipeRegistry.MixPredicate mix : VampirismAPI.extendedBrewingRecipeRegistry().getConversionMixes()) {
            if (mix.reagent1.test(ingredient)) return true;
        }
        return false;
    }

    @Override
    public boolean isInput(ItemStack bottleItem) {
        Item item = bottleItem.getItem();
        return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
    }


}
