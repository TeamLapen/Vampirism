package de.teamlapen.vampirism.common.world.items.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.util.serialization.StreamCodecExtension;
import de.teamlapen.vampirism.common.world.items.component.BloodCharged;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class InfuserRecipe implements Recipe<InfuserRecipe.InfuserRecipeInput> {

    private final CommonInfo commonInfo;
    private final String group;
    private final @Nullable Ingredient ingredient1;
    private final @Nullable Ingredient ingredient2;
    private final @Nullable  Ingredient ingredient3;
    private final @Nullable Ingredient ingredient4;
    private final Ingredient ingredient;
    @Nullable
    private final ItemStackTemplate result1;
    @Nullable
    private final ItemStackTemplate result2;
    @Nullable
    private final ItemStackTemplate result3;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<ItemStackTemplate> result;
    private final int cookingTime;

    @Nullable
    private PlacementInfo placementInfo;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public InfuserRecipe(CommonInfo commonInfo, String groupIn, Optional<Ingredient> ingredient1, Optional<Ingredient> ingredient2, Optional<Ingredient> ingredient3, Optional<Ingredient> ingredient4, Ingredient ingredient, Optional<ItemStackTemplate> result1, Optional<ItemStackTemplate> result2, Optional<ItemStackTemplate> result3, Optional<ItemStackTemplate> result, int cookingTime) {
        this(commonInfo, groupIn, ingredient1.orElse(null), ingredient2.orElse(null), ingredient3.orElse(null), ingredient4.orElse(null), ingredient, result1.orElse(null), result2.orElse(null), result3.orElse(null), result, cookingTime);
    }
    public InfuserRecipe(CommonInfo commonInfo, String groupIn,@Nullable Ingredient ingredient1,@Nullable Ingredient ingredient2,@Nullable Ingredient ingredient3,@Nullable Ingredient ingredient4, Ingredient ingredient, @Nullable ItemStackTemplate result1, @Nullable ItemStackTemplate result2,@Nullable  ItemStackTemplate result3, Optional<ItemStackTemplate> result, int cookingTime) {
        Preconditions.checkArgument(ingredient1 != null || ingredient2 != null || ingredient3 != null || ingredient4 != null, "At least one ingredient must be present");
        this.commonInfo = commonInfo;
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

    public Optional<Ingredient> ingredient1() {
        return Optional.ofNullable(ingredient1);
    }

    public Optional<Ingredient> ingredient2() {
        return Optional.ofNullable(ingredient2);
    }

    public Optional<Ingredient> ingredient3() {
        return Optional.ofNullable(ingredient3);
    }

    public Optional<Ingredient> ingredient4() {
        return Optional.ofNullable(ingredient4);
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ItemStack result1() {
        return result1 != null ? result1.create() : ItemStack.EMPTY;
    }

    public ItemStack result2() {
        return result2 != null ? result2.create() : ItemStack.EMPTY;
    }

    public ItemStack result3() {
        return result3 != null ? result3.create() : ItemStack.EMPTY;
    }

    public Optional<ItemStackTemplate> result() {
        return result;
    }

    public int cookingTime() {
        return cookingTime;
    }

    @Override
    public boolean showNotification() {
        return this.commonInfo.showNotification();
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public boolean matches(InfuserRecipeInput input, Level level) {
        return this.ingredient.test(input.item) &&
                ((this.ingredient1 == null && input.input1.isEmpty()) || (this.ingredient1 != null && this.ingredient1.test(input.input1))) &&
                ((this.ingredient2 == null && input.input2.isEmpty()) || (this.ingredient2 != null && this.ingredient2.test(input.input2))) &&
                ((this.ingredient3 == null && input.input3.isEmpty()) || (this.ingredient3 != null && this.ingredient3.test(input.input3))) &&
                ((this.ingredient4 == null && input.input4.isEmpty()) || (this.ingredient4 != null && this.ingredient4.test(input.input4)));
    }

    @Override
    public ItemStack assemble(InfuserRecipeInput input) {
        int level = Stream.of(input.input1, input.input2, input.input3, input.input4).map(s -> s.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.EMPTY)).mapToInt(PureLevel::level).min().orElse(-1);
        var result = this.result.map(ItemStackTemplate::create).orElseGet(input.item::copy);
        if (result.has(ModDataComponents.BLOOD_CHARGED)) {
            result.set(ModDataComponents.BLOOD_CHARGED, new BloodCharged(1));
        }
        return PureLevel.pureBlood(result, level);
    }

    @Override
    public RecipeSerializer<? extends Recipe<InfuserRecipeInput>> getSerializer() {
        return ModRecipes.INFUSER.get();
    }

    @Override
    public RecipeType<? extends Recipe<InfuserRecipeInput>> getType() {
        return ModRecipes.INFUSER_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(List.of(Optional.ofNullable(this.ingredient1), Optional.ofNullable(this.ingredient2), Optional.ofNullable(this.ingredient3), Optional.ofNullable(this.ingredient4), Optional.of(this.ingredient)));
        }
        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.INFUSER_CATEGORY.get();
    }

    public record InfuserRecipeInput(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, ItemStack item) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
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

    public static final MapCodec<InfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CommonInfo.MAP_CODEC.fieldOf("commoninfo").forGetter(s -> s.commonInfo),
            Codec.STRING.optionalFieldOf("group", "").forGetter(s -> s.group),
            Ingredient.CODEC.optionalFieldOf("ingredient1").forGetter(x -> Optional.ofNullable(x.ingredient1)),
            Ingredient.CODEC.optionalFieldOf("ingredient2").forGetter(x -> Optional.ofNullable(x.ingredient2)),
            Ingredient.CODEC.optionalFieldOf("ingredient3").forGetter(x -> Optional.ofNullable(x.ingredient3)),
            Ingredient.CODEC.optionalFieldOf("ingredient4").forGetter(x -> Optional.ofNullable(x.ingredient4)),
            Ingredient.CODEC.fieldOf("item").forGetter(x -> x.ingredient),
            ItemStackTemplate.CODEC.optionalFieldOf("result1").forGetter(x -> Optional.ofNullable(x.result1)),
            ItemStackTemplate.CODEC.optionalFieldOf("result2").forGetter(x -> Optional.ofNullable(x.result2)),
            ItemStackTemplate.CODEC.optionalFieldOf("result3").forGetter(x -> Optional.ofNullable(x.result3)),
            ItemStackTemplate.CODEC.optionalFieldOf("result").forGetter(x -> x.result),
            Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(x -> x.cookingTime)
    ).apply(instance, InfuserRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> STREAM_CODEC = StreamCodecExtension.composite(
            CommonInfo.STREAM_CODEC, s -> s.commonInfo,
            ByteBufCodecs.STRING_UTF8, s -> s.group,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, s -> Optional.ofNullable(s.ingredient1),
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, s -> Optional.ofNullable(s.ingredient2),
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, s -> Optional.ofNullable(s.ingredient3),
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, s -> Optional.ofNullable(s.ingredient4),
            Ingredient.CONTENTS_STREAM_CODEC, s -> s.ingredient,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), s -> Optional.ofNullable(s.result1),
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), s -> Optional.ofNullable(s.result2),
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), s -> Optional.ofNullable(s.result3),
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), s -> s.result,
            ByteBufCodecs.INT, s -> s.cookingTime,
            InfuserRecipe::new
    );
}
