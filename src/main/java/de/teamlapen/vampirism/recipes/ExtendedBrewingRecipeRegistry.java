package de.teamlapen.vampirism.recipes;

import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.util.ItemDataUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.StreamSupport;

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
    public boolean brewPotions(Level level, @NotNull NonNullList<ItemStack> inputs, @NotNull ItemStack ingredient, @NotNull ItemStack extraIngredient, @NotNull IExtendedBrewingCapabilities capabilities, int @NotNull [] inputIndexes, boolean onlyExtended) {
        boolean brewed = false;
        int useMain = 0;
        int useExtra = 0;
        for (int i : inputIndexes) {
            Optional<Triple<ItemStack, Integer, Integer>> output = getOutput(level, inputs.get(i), ingredient, extraIngredient, capabilities, onlyExtended);
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
    public boolean canBrew(Level level, @NotNull NonNullList<ItemStack> inputs, @NotNull ItemStack ingredient, @NotNull ItemStack extraIngredient, @NotNull IExtendedBrewingCapabilities capabilities, int @NotNull [] inputIndexes) {
        if (ingredient.isEmpty()) return false;

        for (int i : inputIndexes) {
            if (hasOutput(level, inputs.get(i), ingredient, extraIngredient, capabilities)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull Optional<Triple<ItemStack, Integer, Integer>> getOutput(Level level, @NotNull ItemStack bottle, @NotNull ItemStack ingredient, @NotNull ItemStack extraIngredient, @NotNull IExtendedBrewingCapabilities capabilities, boolean onlyExtended) {
        if (bottle.isEmpty() || bottle.getCount() != 1) return Optional.empty();
        if (ingredient.isEmpty()) return Optional.empty();
        PotionContents potion = bottle.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (bottle.getItem() instanceof ThrowablePotionItem && StreamSupport.stream(potion.getAllEffects().spliterator(), false).anyMatch(a -> a.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL)) {
            return Optional.empty();
        }
        Item item = bottle.getItem();
        //Collect mixes that can be brewed with the given ingredients and capabilities
        List<ExtendedPotionMix> possibleResults = new ArrayList<>();
        for (ExtendedPotionMix mix : conversionMixes) {
            if (potion.is(mix.input) && mix.reagent1.get().test(ingredient) && ingredient.getCount() >= mix.reagent1Count && (mix.reagent2Count <= 0 || (mix.reagent2.get().test(extraIngredient) && extraIngredient.getCount() >= mix.reagent2Count)) && mix.canBrew(capabilities)) {
                possibleResults.add(mix);
            }
        }
        if (!possibleResults.isEmpty()) {
            //Make sure to use the efficient version if multiple mixes have been found
            possibleResults.sort((mix1, mix2) ->
                    mix1.efficient ? (mix2.efficient ? 0 : -1) : (mix2.efficient ? 1 : 0)
            );
            ExtendedPotionMix mix = possibleResults.getFirst();
            return Optional.of(Triple.of(ItemDataUtils.setPotion(new ItemStack(item), mix.output), mix.reagent1Count, mix.reagent2Count));

        }
        ItemStack output = level.potionBrewing().mix(bottle, ingredient);
        return output.isEmpty() ? Optional.empty() : Optional.of(Triple.of(output, 1, 0));
    }

    @Override
    public @NotNull List<ExtendedPotionMix> getPotionMixes() {
        return Collections.unmodifiableList(conversionMixes);
    }


    @Override
    public boolean hasOutput(Level level, @NotNull ItemStack input, @NotNull ItemStack ingredient, @NotNull ItemStack extraIngredient, @NotNull IExtendedBrewingCapabilities capabilities) {
        return getOutput(level, input, ingredient, extraIngredient, capabilities, false).isPresent();
    }

    @Override
    public boolean isValidExtraIngredient(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (ExtendedPotionMix mix : conversionMixes) {
            if (mix.reagent2.get().test(stack)) return true;

        }

        return false;
    }

    @Override
    public boolean isValidIngredient(PotionBrewing registry, @NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (ExtendedPotionMix mix : conversionMixes) {
            if (mix.reagent1.get().test(stack)) return true;
        }
        return registry.isIngredient(stack);
    }

    @Override
    public boolean isValidInput(PotionBrewing registry, @NotNull ItemStack stack) {
        if (stack.getCount() != 1) return false;

        Item item = stack.getItem();
        return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE || registry.isInput(stack);
    }
}
