package de.teamlapen.vampirism.inventory.recipes;

import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;


public class ExtendedBrewingRecipeRegistry implements IExtendedBrewingRecipeRegistry {

    private final List<ExtendedPotionMix> conversionMixes = new ArrayList<>();


    @Override
    public void addMix(ExtendedPotionMix potionMix) {
        this.conversionMixes.add(potionMix);
    }

    @Override
    public void addMix(ExtendedPotionMix[] mixPredicate) {
        this.conversionMixes.addAll(Arrays.asList(mixPredicate));
    }


    @Override
    public boolean brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes, boolean onlyExtended) {
        boolean brewed = false;
        int useMain = 0;
        int useExtra = 0;
        for (int i : inputIndexes) {
            Optional<Triple<ItemStack, Integer, Integer>> output = getOutput(inputs.get(i), ingredient, extraIngredient, capabilities, onlyExtended);
            if (output.isPresent()) {
                Triple<ItemStack, Integer, Integer> triple = output.get();
                inputs.set(i, triple.getLeft());
                useMain = Math.max(useMain, triple.getMiddle());
                useExtra = Math.max(useExtra, triple.getRight());
                brewed = true;
            }
        }
        ingredient.shrink(useMain);
        extraIngredient.shrink(useExtra);
        return brewed;
    }

    @Override
    public boolean canBrew(NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes) {
        if (ingredient.isEmpty()) return false;

        for (int i : inputIndexes) {
            if (hasOutput(inputs.get(i), ingredient, extraIngredient, capabilities)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Optional<Triple<ItemStack, Integer, Integer>> getOutput(ItemStack bottle, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, boolean onlyExtended) {
        if (bottle.isEmpty() || bottle.getCount() != 1) return Optional.empty();
        if (ingredient.isEmpty()) return Optional.empty();
        Potion potion = PotionUtils.getPotionFromItem(bottle);
        Item item = bottle.getItem();
        for (ExtendedPotionMix mix : conversionMixes) {
            if (mix.input.get() == potion && mix.reagent1.test(ingredient) && ingredient.getCount() >= mix.reagent1Count && (mix.reagent2Count <= 0 || (mix.reagent2.test(extraIngredient) && extraIngredient.getCount() >= mix.reagent2Count)) && mix.canBrew(capabilities)) {
                return Optional.of(Triple.of(PotionUtils.addPotionToItemStack(new ItemStack(item), mix.output.get()), mix.reagent1Count, mix.reagent2Count));
            }
        }
        ItemStack output = BrewingRecipeRegistry.getOutput(bottle, ingredient);
        return output.isEmpty() ? Optional.empty() : Optional.of(Triple.of(output, 1, 0));
    }

    @Override
    public List<ExtendedPotionMix> getPotionMixes() {
        return Collections.unmodifiableList(conversionMixes);
    }


    @Override
    public boolean hasOutput(ItemStack input, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities) {
        return getOutput(input, ingredient, extraIngredient, capabilities, false).isPresent();
    }

    @Override
    public boolean isValidExtraIngredient(ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (ExtendedPotionMix mix : conversionMixes) {
            if (mix.reagent2.test(stack)) return true;

        }

        return false;
    }

    @Override
    public boolean isValidIngredient(ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (ExtendedPotionMix mix : conversionMixes) {
            if (mix.reagent1.test(stack)) return true;
        }
        return BrewingRecipeRegistry.isValidIngredient(stack);
    }

    @Override
    public boolean isValidInput(ItemStack stack) {
        if (stack.getCount() != 1) return false;

        Item item = stack.getItem();
        return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE || BrewingRecipeRegistry.isValidIngredient(stack);
    }
}
