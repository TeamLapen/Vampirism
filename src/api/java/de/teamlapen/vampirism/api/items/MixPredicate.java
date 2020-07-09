package de.teamlapen.vampirism.api.items;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.function.Predicate;

public class MixPredicate {
    public final net.minecraftforge.registries.IRegistryDelegate<Potion> input;
    public final Ingredient reagent1;
    public final int reagent1Count;
    public final Ingredient reagent2;
    public final int reagent2Count;
    public final net.minecraftforge.registries.IRegistryDelegate<Potion> output;
    public final Predicate<IExtendedBrewingRecipeRegistry.IExtendedBrewingCapabilities> condition;

    private MixPredicate(IRegistryDelegate<Potion> inputIn, Ingredient reagentIn1, int count1, Ingredient reagentIn2, int count2, IRegistryDelegate<Potion> outputIn, Predicate<IExtendedBrewingRecipeRegistry.IExtendedBrewingCapabilities> condition) {
        this.input = inputIn;
        this.reagent1 = reagentIn1;
        this.reagent1Count = count1;
        this.reagent2 = reagentIn2;
        this.reagent2Count = count2;
        this.output = outputIn;
        this.condition = condition;
    }


    public static class Builder {

        private final net.minecraftforge.registries.IRegistryDelegate<Potion> input;
        private final net.minecraftforge.registries.IRegistryDelegate<Potion> output;
        private Ingredient reagent1 = Ingredient.EMPTY;
        private int reagent1Count = -0;
        private int reagent1CountReduced = -0;
        private Ingredient reagent2 = Ingredient.EMPTY;
        private int reagent2Count = -1;
        private int reagent2CountReduced = -1;
        private boolean durable = false;
        private boolean concentrated = false;
        private boolean master = false;

        public Builder(Potion input, Potion output) {
            this.input = input.delegate;
            this.output = output.delegate;
        }

        public MixPredicate[] build() {
            boolean efficient = reagent1CountReduced != -1 || reagent2CountReduced != -1;
            MixPredicate[] result = new MixPredicate[efficient ? 2 : 1];
            result[0] = new MixPredicate(input, reagent1Count == 0 ? Ingredient.EMPTY : reagent1, reagent1Count, reagent2Count == 0 ? Ingredient.EMPTY : reagent2, reagent2Count, output, cap -> (!master || cap.isMasterBrewing()) && (!durable || cap.isDurableBrewing()) && (!concentrated || cap.isConcentratedBrewing()) && (!efficient || !cap.isEfficientBrewing()));
            if (efficient) {
                result[1] = new MixPredicate(input, reagent1Count == 0 || reagent2CountReduced == 0 ? Ingredient.EMPTY : reagent1, reagent1CountReduced != -1 ? reagent1CountReduced : reagent1Count, reagent2Count == 0 || reagent2CountReduced == 0 ? Ingredient.EMPTY : reagent2, reagent2Count != -1 ? reagent2CountReduced : reagent2Count, output, cap -> (!master || cap.isMasterBrewing()) && (!durable || cap.isDurableBrewing()) && (!concentrated || cap.isConcentratedBrewing()) && (cap.isEfficientBrewing()));
            }
            return result;
        }

        public Builder ingredient(Ingredient i) {
            this.reagent1 = i;
            this.reagent1Count = 1;
            return this;
        }

        public Builder ingredient(Ingredient i, int count) {
            this.reagent1 = i;
            this.reagent1Count = count;
            return this;
        }

        public Builder ingredient(Ingredient i, int count, int reducedCount) {
            this.reagent1 = i;
            this.reagent1Count = count;
            this.reagent1CountReduced = reducedCount;
            return this;
        }

        public Builder extraIngredient(Ingredient i) {
            this.reagent2 = i;
            this.reagent2Count = 1;
            return this;
        }

        public Builder extraIngredient(Ingredient i, int count) {
            this.reagent2 = i;
            this.reagent2Count = count;
            return this;
        }

        public Builder extraIngredient(Ingredient i, int count, int countReduced) {
            this.reagent2 = i;
            this.reagent2Count = count;
            this.reagent2CountReduced = countReduced;
            return this;
        }

        public Builder concentrated() {
            this.concentrated = true;
            return this;
        }

        public Builder durable() {
            this.durable = true;
            return this;
        }

        public Builder master() {
            this.master = true;
            return this;
        }


    }
}
