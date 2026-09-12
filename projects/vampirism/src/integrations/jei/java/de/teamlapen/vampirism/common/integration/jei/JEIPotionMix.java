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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JEIPotionMix {

    public static @NotNull List<JEIPotionMix> createFromMixes(@NotNull Collection<ExtendedPotionMix> mixes) {
        Map<MixGroup, List<ExtendedPotionMix>> grouped = new LinkedHashMap<>();
        for (ExtendedPotionMix mix : mixes) {
            grouped.computeIfAbsent(MixGroup.of(mix), key -> new ArrayList<>()).add(mix);
        }
        List<JEIPotionMix> recipes = new ArrayList<>(grouped.size() * 3);
        for (List<ExtendedPotionMix> group : grouped.values()) {
            recipes.addAll(createFromGroup(group));
        }
        return recipes;
    }

    private static @NotNull Collection<JEIPotionMix> createFromGroup(@NotNull List<ExtendedPotionMix> group) {
        ExtendedPotionMix full = group.stream().filter(mix -> !mix.efficient).findFirst().orElse(group.getFirst());
        ExtendedPotionMix reduced = group.stream().filter(mix -> mix.efficient).findFirst().orElse(null);

        IngredientWithAmounts mix1 = new IngredientWithAmounts(full.reagent1.get(), amounts(full.reagent1Count, reduced == null ? -1 : reduced.reagent1Count));
        IngredientWithAmounts mix2 = new IngredientWithAmounts(full.reagent2.get(), amounts(full.reagent2Count, reduced == null ? -1 : reduced.reagent2Count));

        List<JEIPotionMix> recipes = new ArrayList<>(3);

        var potion = build(full, reduced, Items.POTION, full.input, full.output, mix1, mix2);
        PotionBrewingStepCounter.INSTANCE.addVaporStillRecipe(potion.getPotionInput(), potion.getPotionOutput());
        recipes.add(potion);

        if (full.output.value().getEffects().stream().noneMatch(s -> s.getEffect().value().getCategory() != MobEffectCategory.HARMFUL)) {
            var splash = build(full, reduced, Items.SPLASH_POTION, full.input, full.output, mix1, mix2);
            PotionBrewingStepCounter.INSTANCE.addVaporStillRecipe(splash.getPotionInput(), splash.getPotionOutput());
            recipes.add(splash);

            var lingering = build(full, reduced, Items.LINGERING_POTION, full.input, full.output, mix1, mix2);
            PotionBrewingStepCounter.INSTANCE.addVaporStillRecipe(lingering.getPotionInput(), lingering.getPotionOutput());
            recipes.add(lingering);
        }
        return recipes;
    }

    private static @NotNull List<Integer> amounts(int fullCount, int reducedCount) {
        if (reducedCount < 0 || reducedCount == fullCount) {
            return List.of(fullCount);
        }

        return List.of(fullCount, reducedCount);
    }

    private static @NotNull JEIPotionMix build(ExtendedPotionMix full, @Nullable ExtendedPotionMix reduced, Item base, @NotNull Holder<Potion> in, @NotNull Holder<Potion> out, IngredientWithAmounts in1, IngredientWithAmounts in2) {
        ItemStack potionIn = PotionContents.createItemStack(base, in);
        ItemStack potionOut = PotionContents.createItemStack(base, out);
        return new JEIPotionMix(full, reduced, potionIn, in1, in2, potionOut);
    }

    private final ItemStack potionInput;
    private final IngredientWithAmounts mix1;
    private final IngredientWithAmounts mix2;
    private final ItemStack potionOutput;
    private final ExtendedPotionMix original;
    private final @Nullable ExtendedPotionMix reduced;

    private JEIPotionMix(ExtendedPotionMix original, @Nullable ExtendedPotionMix reduced, ItemStack potionInput, IngredientWithAmounts mix1, IngredientWithAmounts mix2, ItemStack potionOutput) {
        this.original = original;
        this.reduced = reduced;
        this.potionInput = potionInput;
        this.mix1 = mix1;
        this.mix2 = mix2;
        this.potionOutput = potionOutput;
    }

    public ExtendedPotionMix getOriginal() {
        return original;
    }

    public boolean hasReducedCost() {
        return reduced != null;
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

    public List<Integer> getMix1Amounts() {
        return mix1.amounts();
    }

    public List<Integer> getMix2Amounts() {
        return mix2.amounts();
    }

    public ItemStack getPotionOutput() {
        return potionOutput;
    }

    public int getBrewingSteps() {
        return PotionBrewingStepCounter.INSTANCE.getBrewingSteps(potionOutput);
    }

    private record IngredientWithAmounts(Ingredient ingredient, List<Integer> amounts) {}

    private record MixGroup(Holder<Potion> input, Holder<Potion> output, Ingredient reagent1, Ingredient reagent2, boolean durable, boolean concentrated, boolean master, boolean sovereign) {

        private static MixGroup of(ExtendedPotionMix mix) {
            return new MixGroup(mix.input, mix.output, mix.reagent1.get(), mix.reagent2.get(), mix.durable, mix.concentrated, mix.master, mix.sovereign);
        }
    }
}
