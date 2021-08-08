package de.teamlapen.vampirism.modcompat.jei;


import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JEIPotionMix {
    public static Collection<JEIPotionMix> createFromMix(ExtendedPotionMix mix) {
        List<ItemStack> in1 = mix.reagent1.map(Ingredient::getItems).map(Arrays::stream).orElse(Stream.empty()).map(ItemStack::copy).peek(s -> s.setCount(mix.reagent1Count)).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<ItemStack> in2 = mix.reagent2.map(Ingredient::getItems).map(Arrays::stream).orElse(Stream.empty()).map(ItemStack::copy).peek(s -> s.setCount(mix.reagent2Count)).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<JEIPotionMix> recipes = new ArrayList<>(3);
        recipes.add(build(mix, Items.POTION, mix.input.get(), mix.output.get(), in1, in2));
        if (mix.output.get().getEffects().stream().noneMatch(s -> s.getEffect().getCategory() != MobEffectCategory.HARMFUL)) {
            recipes.add(build(mix, Items.LINGERING_POTION, mix.input.get(), mix.output.get(), in1, in2));
            recipes.add(build(mix, Items.SPLASH_POTION, mix.input.get(), mix.output.get(), in1, in2));
        }
        return recipes;
    }

    private static JEIPotionMix build(ExtendedPotionMix mix, Item base, Potion in, Potion out, List<ItemStack> in1, List<ItemStack> in2) {
        List<List<ItemStack>> ingredientList = new ArrayList<>();
        ItemStack potionIn = PotionUtils.setPotion(new ItemStack(base), in);
        ItemStack potionOut = PotionUtils.setPotion(new ItemStack(base), out);
        List<ItemStack> potionInList = Collections.singletonList(potionIn);
        ingredientList.add(potionInList);
        ingredientList.add(potionInList);
        ingredientList.add(potionInList);
        ingredientList.add(in1);
        ingredientList.add(in2);
        return new JEIPotionMix(mix, ingredientList, potionOut);
    }

    private final List<List<ItemStack>> inputs;
    private final ItemStack potionOutput;
    private final ExtendedPotionMix original;

    private JEIPotionMix(ExtendedPotionMix original, List<List<ItemStack>> inputs, ItemStack potionOutput) {
        this.inputs = inputs;
        this.potionOutput = potionOutput;
        this.original = original;
    }

    public List<List<ItemStack>> getInputs() {
        return inputs;
    }

    public ExtendedPotionMix getOriginal() {
        return original;
    }

    public ItemStack getPotionOutput() {
        return potionOutput;
    }
}
