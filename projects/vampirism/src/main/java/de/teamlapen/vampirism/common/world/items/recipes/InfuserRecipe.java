package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.util.serialization.StreamCodecExtension;
import de.teamlapen.vampirism.common.world.items.component.BloodCharged;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class InfuserRecipe implements Recipe<InfuserRecipe.InfuserRecipeInput> {

    @NotNull
    private final String group;
    private final Ingredient ingredient1;
    private final Ingredient ingredient2;
    private final Ingredient ingredient3;
    private final Ingredient ingredient4;
    private final Ingredient ingredient;
    private final ItemStack result1;
    private final ItemStack result2;
    private final ItemStack result3;
    private final Optional<ItemStack> result;
    private final int cookingTime;

    @Nullable
    private PlacementInfo placementInfo;

    public InfuserRecipe(@NotNull String groupIn, Ingredient ingredient1, Ingredient ingredient2, Ingredient ingredient3, Ingredient ingredient4, Ingredient ingredient, ItemStack result1, ItemStack result2, ItemStack result3, Optional<ItemStack> result, int cookingTime) {
        this.group = groupIn;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.ingredient3 = ingredient3;
        this.ingredient4 = ingredient4;
        this.ingredient = ingredient;
        this.result1 = result1;
        this.result2 = result2;
        this.result3 = result3;
        this.result = result;
        this.cookingTime = cookingTime;
    }

    public Ingredient ingredient1() {
        return ingredient1;
    }

    public Ingredient ingredient2() {
        return ingredient2;
    }

    public Ingredient ingredient3() {
        return ingredient3;
    }

    public Ingredient ingredient4() {
        return ingredient4;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ItemStack result1() {
        return result1;
    }

    public ItemStack result2() {
        return result2;
    }

    public ItemStack result3() {
        return result3;
    }

    public Optional<ItemStack> result() {
        return result;
    }

    public int cookingTime() {
        return cookingTime;
    }

    @Override
    public boolean matches(InfuserRecipeInput input, @NotNull Level level) {
        return this.ingredient.test(input.item) && this.ingredient1.test(input.input1) && this.ingredient2.test(input.input2) && this.ingredient3.test(input.input3) && this.ingredient4.test(input.input4);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull InfuserRecipeInput input, HolderLookup.@NotNull Provider registries) {
        int level = Stream.of(input.input1, input.input2, input.input3, input.input4).map(s -> s.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.EMPTY)).mapToInt(PureLevel::level).min().orElse(-1);
        var result = this.result.map(ItemStack::copy).orElseGet(input.item::copy);
        if (result.has(ModDataComponents.BLOOD_CHARGED)) {
            result.set(ModDataComponents.BLOOD_CHARGED, new BloodCharged(1));
        }
        return PureLevel.pureBlood(result, level);
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<InfuserRecipeInput>> getSerializer() {
        return ModRecipes.INFUSER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<InfuserRecipeInput>> getType() {
        return ModRecipes.INFUSER_TYPE.get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(List.of(this.ingredient1, this.ingredient2, this.ingredient3, this.ingredient4, this.ingredient));
        }
        return this.placementInfo;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return ModRecipes.INFUSER_CATEGORY.get();
    }

    public record InfuserRecipeInput(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, ItemStack item) implements RecipeInput {

        @Override
        public @NotNull ItemStack getItem(int index) {
            return switch (index) {
                case 0 -> this.input1;
                case 1 -> this.input2;
                case 2 -> this.input3;
                case 3 -> this.input4;
                case 4 -> this.item;
                default -> throw new IllegalArgumentException("Recipe does not contain slot " + index);
            };
        }

        @Override
        public int size() {
            return 5;
        }
    }

    public static class Serializer implements RecipeSerializer<InfuserRecipe> {

        public static final MapCodec<InfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(s -> s.group),
                Ingredient.CODEC.fieldOf("ingredient1").forGetter(x -> x.ingredient1),
                Ingredient.CODEC.fieldOf("ingredient2").forGetter(x -> x.ingredient2),
                Ingredient.CODEC.fieldOf("ingredient3").forGetter(x -> x.ingredient3),
                Ingredient.CODEC.fieldOf("ingredient4").forGetter(x -> x.ingredient4),
                Ingredient.CODEC.fieldOf("item").forGetter(x -> x.ingredient),
                ItemStack.CODEC.optionalFieldOf("result1", ItemStack.EMPTY).forGetter(x -> x.result1),
                ItemStack.CODEC.optionalFieldOf("result2", ItemStack.EMPTY).forGetter(x -> x.result2),
                ItemStack.CODEC.optionalFieldOf("result3", ItemStack.EMPTY).forGetter(x -> x.result3),
                ItemStack.CODEC.optionalFieldOf("result").forGetter(x -> x.result),
                Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(x -> x.cookingTime)
        ).apply(instance, InfuserRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> STREAM_CODEC = StreamCodecExtension.composite(
                ByteBufCodecs.STRING_UTF8, s -> s.group,
                Ingredient.CONTENTS_STREAM_CODEC, s -> s.ingredient1,
                Ingredient.CONTENTS_STREAM_CODEC, s -> s.ingredient2,
                Ingredient.CONTENTS_STREAM_CODEC, s -> s.ingredient3,
                Ingredient.CONTENTS_STREAM_CODEC, s -> s.ingredient4,
                Ingredient.CONTENTS_STREAM_CODEC, s -> s.ingredient,
                ItemStack.OPTIONAL_STREAM_CODEC, s -> s.result1,
                ItemStack.OPTIONAL_STREAM_CODEC, s -> s.result2,
                ItemStack.OPTIONAL_STREAM_CODEC, s -> s.result3,
                ByteBufCodecs.optional(ItemStack.STREAM_CODEC), s -> s.result,
                ByteBufCodecs.INT, s -> s.cookingTime,
                InfuserRecipe::new
        );

        @Override
        public @NotNull MapCodec<InfuserRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
