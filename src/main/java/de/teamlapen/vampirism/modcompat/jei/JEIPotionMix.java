package de.teamlapen.vampirism.modcompat.jei;


import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JEIPotionMix {
    public static @NotNull Collection<JEIPotionMix> createFromMix(@NotNull ExtendedPotionMix mix) {
        List<ItemStack> in1 = Arrays.stream(mix.reagent1.get().getItems()).map(ItemStack::copy).peek(s -> s.setCount(mix.reagent1Count)).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<ItemStack> in2 = Arrays.stream(mix.reagent2.get().getItems()).map(ItemStack::copy).peek(s -> s.setCount(mix.reagent2Count)).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<JEIPotionMix> recipes = new ArrayList<>(3);
        recipes.add(build(mix, Items.POTION, mix.input.get(), mix.output.get(), in1, in2));
        if (mix.output.get().getEffects().stream().noneMatch(s -> s.getEffect().getCategory() != MobEffectCategory.HARMFUL)) {
            recipes.add(build(mix, Items.LINGERING_POTION, mix.input.get(), mix.output.get(), in1, in2));
            recipes.add(build(mix, Items.SPLASH_POTION, mix.input.get(), mix.output.get(), in1, in2));
        }
        return recipes;
    }

    private static @NotNull JEIPotionMix build(ExtendedPotionMix mix, Item base, @NotNull Potion in, @NotNull Potion out, List<ItemStack> in1, List<ItemStack> in2) {
        ItemStack potionIn = PotionUtils.setPotion(new ItemStack(base), in);
        ItemStack potionOut = PotionUtils.setPotion(new ItemStack(base), out);
        return new JEIPotionMix(mix, potionIn, in1, in2, potionOut);
    }

    private final ItemStack potionInput;
    private final ItemStack potionOutput;
    private final List<ItemStack> mix1;
    private final List<ItemStack> mix2;
    private final ExtendedPotionMix original;

    private JEIPotionMix(ExtendedPotionMix original, ItemStack potionInput, List<ItemStack> mix1, List<ItemStack> mix2, ItemStack potionOutput) {
        this.original = original;
        this.potionInput = potionInput;
        this.mix1 = mix1;
        this.mix2 = mix2;
        this.potionOutput = potionOutput;
    }

    public ExtendedPotionMix getOriginal() {
        return original;
    }

    public ItemStack getPotionInput() {
        return potionInput;
    }

    public List<ItemStack> getMix1() {
        return mix1;
    }

    public List<ItemStack> getMix2() {
        return mix2;
    }

    public ItemStack getPotionOutput() {
        return potionOutput;
    }
}
