package de.teamlapen.vampirism.common.integration.jei;


import de.teamlapen.vampirism.api.world.items.ExtendedPotionMix;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JEIPotionMix {
    public static @NotNull Collection<JEIPotionMix> createFromMix(@NotNull ExtendedPotionMix mix) {

        List<JEIPotionMix> recipes = new ArrayList<>(3);
        recipes.add(build(mix, Items.POTION, mix.input, mix.output, new IngredientWithAmount(mix.reagent1.get(), mix.reagent1Count), new IngredientWithAmount(mix.reagent2.get(), mix.reagent2Count)));
        if (mix.output.value().getEffects().stream().noneMatch(s -> s.getEffect().value().getCategory() != MobEffectCategory.HARMFUL)) {
            recipes.add(build(mix, Items.LINGERING_POTION, mix.input, mix.output, new IngredientWithAmount(mix.reagent1.get(), mix.reagent1Count), new IngredientWithAmount(mix.reagent2.get(), mix.reagent2Count)));
            recipes.add(build(mix, Items.SPLASH_POTION, mix.input, mix.output, new IngredientWithAmount(mix.reagent1.get(), mix.reagent1Count), new IngredientWithAmount(mix.reagent2.get(), mix.reagent2Count)));
        }
        return recipes;
    }

    private static @NotNull JEIPotionMix build(ExtendedPotionMix mix, Item base, @NotNull Holder<Potion> in, @NotNull Holder<Potion> out, IngredientWithAmount in1, IngredientWithAmount in2) {
        ItemStack potionIn = PotionContents.createItemStack(base, in);
        ItemStack potionOut = PotionContents.createItemStack(base, out);
        return new JEIPotionMix(mix, potionIn, in1, in2, potionOut);
    }

    private final ItemStack potionInput;
    private final IngredientWithAmount mix1;
    private final IngredientWithAmount mix2;
    private final ItemStack potionOutput;
    private final ExtendedPotionMix original;

    private JEIPotionMix(ExtendedPotionMix original, ItemStack potionInput, IngredientWithAmount mix1, IngredientWithAmount mix2, ItemStack potionOutput) {
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

    public Ingredient getMix1() {
        return mix1.ingredient();
    }

    public Ingredient getMix2() {
        return mix2.ingredient();
    }

    public int getMix1Amount() {
        return mix1.amount();
    }

    public int getMix2Amount() {
        return mix2.amount();
    }

    public ItemStack getPotionOutput() {
        return potionOutput;
    }

    private record IngredientWithAmount(Ingredient ingredient, int amount) {}

}
