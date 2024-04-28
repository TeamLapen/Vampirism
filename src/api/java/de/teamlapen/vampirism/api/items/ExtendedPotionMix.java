package de.teamlapen.vampirism.api.items;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ExtendedPotionMix {
    public final Holder<Potion> input;
    public final Supplier<Ingredient> reagent1;
    public final int reagent1Count;
    public final Supplier<Ingredient> reagent2;
    public final int reagent2Count;
    public final Holder<Potion> output;
    public final boolean durable;
    public final boolean concentrated;
    public final boolean master;
    public final boolean efficient;

    private ExtendedPotionMix(Holder<Potion> inputIn, Supplier<Ingredient> reagentIn1, int count1, Supplier<Ingredient> reagentIn2, int count2, Holder<Potion> outputIn, boolean durable, boolean concentrated, boolean master, boolean efficient) {
        this.input = inputIn;
        this.reagent1 = reagentIn1;
        this.reagent1Count = count1;
        this.reagent2 = reagentIn2;
        this.reagent2Count = count2;
        this.output = outputIn;
        this.durable = durable;
        this.concentrated = concentrated;
        this.master = master;
        this.efficient = efficient;
    }

    public boolean canBrew(IExtendedBrewingRecipeRegistry.@NotNull IExtendedBrewingCapabilities cap) {
        return (!master || cap.isMasterBrewing()) && (!durable || cap.isDurableBrewing()) && (!concentrated || cap.isConcentratedBrewing()) && (!efficient || cap.isEfficientBrewing());
    }


    public static class Builder {
        private final static Supplier<Ingredient> EMPTY_SUPPLIER = () -> Ingredient.EMPTY;
        private static final Supplier<Ingredient> VAMPIRE_BLOOD = () -> Ingredient.of(BuiltInRegistries.ITEM.get(new ResourceLocation("vampirism", "vampire_blood_bottle")));
        private final Holder<Potion> input;
        private final Holder<Potion> output;
        private @NotNull Supplier<Ingredient> reagent1 = EMPTY_SUPPLIER;
        private int reagent1Count = 0;
        private int reagent1CountReduced = -1;
        private @NotNull Supplier<Ingredient> reagent2 = EMPTY_SUPPLIER;
        private int reagent2Count = 0;
        private int reagent2CountReduced = -1;
        private boolean durable = false;
        private boolean concentrated = false;
        private boolean master = false;

        public Builder(Holder<Potion> input, Holder<Potion> output) {
            this.input = input;
            this.output = output;
        }

        public Builder blood() {
            return this.extraIngredient(VAMPIRE_BLOOD);
        }

        public ExtendedPotionMix @NotNull [] build() {
            boolean efficient = reagent1CountReduced != -1 || reagent2CountReduced != -1;
            ExtendedPotionMix[] result = new ExtendedPotionMix[efficient ? 2 : 1];
            result[0] = new ExtendedPotionMix(input, reagent1Count == 0 ? EMPTY_SUPPLIER : reagent1, reagent1Count, reagent2Count == 0 ? EMPTY_SUPPLIER : reagent2, reagent2Count, output, durable, concentrated, master, false);
            if (efficient) {
                result[1] = new ExtendedPotionMix(input, reagent1Count == 0 || reagent1CountReduced == 0 ? EMPTY_SUPPLIER : reagent1, reagent1CountReduced != -1 ? reagent1CountReduced : reagent1Count, reagent2Count == 0 || reagent2CountReduced == 0 ? EMPTY_SUPPLIER : reagent2, reagent2CountReduced != -1 ? reagent2CountReduced : reagent2Count, output, durable, concentrated, master, true);
            }
            return result;
        }

        public @NotNull Builder concentrated() {
            this.concentrated = true;
            return this;
        }

        public @NotNull Builder durable() {
            this.durable = true;
            return this;
        }

        public @NotNull Builder extraIngredient(Supplier<Ingredient> i) {
            this.reagent2 = i;
            this.reagent2Count = 1;
            return this;
        }

        public @NotNull Builder extraIngredient(Supplier<Ingredient> i, int count) {
            this.reagent2 = i;
            this.reagent2Count = count;
            return this;
        }

        public @NotNull Builder extraIngredient(Supplier<Ingredient> i, int count, int countReduced) {
            this.reagent2 = i;
            this.reagent2Count = count;
            this.reagent2CountReduced = countReduced;
            return this;
        }

        public @NotNull Builder ingredient(Supplier<Ingredient> i) {
            this.reagent1 = i;
            this.reagent1Count = 1;
            return this;
        }

        public @NotNull Builder ingredient(Supplier<Ingredient> i, int count) {
            this.reagent1 = i;
            this.reagent1Count = count;
            return this;
        }

        public @NotNull Builder ingredient(Supplier<Ingredient> i, int count, int reducedCount) {
            this.reagent1 = i;
            this.reagent1Count = count;
            this.reagent1CountReduced = reducedCount;
            return this;
        }

        public @NotNull Builder master() {
            this.master = true;
            return this;
        }


    }
}
