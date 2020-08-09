package de.teamlapen.vampirism.modcompat.jei;


import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class JEIPotionMix {
    public static Collection<JEIPotionMix> createFromMix(ExtendedPotionMix mix) {
        List<ItemStack> in1 = Arrays.stream(mix.reagent1.getMatchingStacks()).map(ItemStack::copy).peek(s -> s.setCount(mix.reagent1Count)).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<ItemStack> in2 = Arrays.stream(mix.reagent2.getMatchingStacks()).map(ItemStack::copy).peek(s -> s.setCount(mix.reagent2Count)).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<JEIPotionMix> recipes = new ArrayList<>(3);
        recipes.add(build(mix, Items.POTION, mix.input.get(), mix.output.get(), in1, in2));
        recipes.add(build(mix, Items.LINGERING_POTION, mix.input.get(), mix.output.get(), in1, in2));
        recipes.add(build(mix, Items.SPLASH_POTION, mix.input.get(), mix.output.get(), in1, in2));
        return recipes;
    }

    private static JEIPotionMix build(ExtendedPotionMix mix, Item base, Potion in, Potion out, List<ItemStack> in1, List<ItemStack> in2) {
        List<List<ItemStack>> ingredientList = new ArrayList<>();
        ItemStack potionIn = PotionUtils.addPotionToItemStack(new ItemStack(base), in);
        ItemStack potionOut = PotionUtils.addPotionToItemStack(new ItemStack(base), out);
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
