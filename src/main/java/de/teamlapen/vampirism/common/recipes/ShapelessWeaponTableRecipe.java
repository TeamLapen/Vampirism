package de.teamlapen.vampirism.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.serialization.StreamCodecExtension;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;


public class ShapelessWeaponTableRecipe implements Recipe<CraftingInput>, IWeaponTableRecipe {
    protected static final int MAX_WIDTH = 4;
    protected static final int MAX_HEIGHT = 4;

    private final @NotNull CraftingBookCategory category;
    private final @NotNull String group;
    private final @NotNull List<Ingredient> recipeItems;
    private final @NotNull ItemStack recipeOutput;
    private final int requiredLevel;
    private final List<Holder<ISkill<?>>> requiredSkills;
    private final int requiredLava;
    private final boolean isSimple;
    @Nullable
    private PlacementInfo placementInfo;

    public ShapelessWeaponTableRecipe(@NotNull String group, @NotNull CraftingBookCategory category, @NotNull List<Ingredient> ingredients, @NotNull ItemStack result, int level, int lava, @NotNull List<Holder<ISkill<?>>> skills) {
        this.category = category;
        this.group = group;
        this.recipeItems = ingredients;
        this.recipeOutput = result;
        this.requiredLevel = level;
        this.requiredLava = lava;
        this.requiredSkills = skills;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingInput inv, @NotNull HolderLookup.Provider registryAccess) {
        return this.recipeOutput.copy();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(this.recipeItems);
        }
        return placementInfo;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return ModRecipes.WEAPON_TABLE_CATEGORY.get();
    }

    @NotNull
    @Override
    public List<Ingredient> getIngredients() {
        return recipeItems;
    }

    public int getRequiredLavaUnits() {
        return requiredLava;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    @NotNull
    @Override
    public List<Holder<ISkill<?>>> getRequiredSkills() {
        return requiredSkills;
    }

    @NotNull
    @Override
    public RecipeSerializer<ShapelessWeaponTableRecipe> getSerializer() {
        return ModRecipes.SHAPELESS_CRAFTING_WEAPONTABLE.get();
    }

    @NotNull
    @Override
    public RecipeType<IWeaponTableRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != this.recipeItems.size()) {
            return false;
        } else if (!isSimple) {
            var nonEmptyItems = new java.util.ArrayList<ItemStack>(input.ingredientCount());
            for (var item : input.items())
                if (!item.isEmpty())
                    nonEmptyItems.add(item);
            return net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(nonEmptyItems, this.recipeItems) != null;
        } else {
            return input.size() == 1 && this.recipeItems.size() == 1
                    ? this.recipeItems.getFirst().test(input.getItem(0))
                    : input.stackedContents().canCraft(this, null);
        }
    }

    public static class Serializer implements RecipeSerializer<ShapelessWeaponTableRecipe> {

        public static final MapCodec<ShapelessWeaponTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(p_301127_ -> p_301127_.group),
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_301133_ -> p_301133_.category),
                    Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, MAX_WIDTH * MAX_HEIGHT)).fieldOf("ingredients").forGetter(x -> x.recipeItems),
                    ItemStack.CODEC.fieldOf("result").forGetter(p_301142_ -> p_301142_.recipeOutput),
                    Codec.INT.optionalFieldOf("level", 1).forGetter(p -> p.requiredLevel),
                    Codec.INT.optionalFieldOf("lava", 0).forGetter(p -> p.requiredLava),
                    ModRegistries.SKILLS.holderByNameCodec().listOf().optionalFieldOf("skill", Collections.emptyList()).forGetter(p -> p.requiredSkills)
            ).apply(inst, ShapelessWeaponTableRecipe::new);
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessWeaponTableRecipe> STREAM_CODEC = StreamCodecExtension.composite(
                ByteBufCodecs.STRING_UTF8, s -> s.group,
                CraftingBookCategory.STREAM_CODEC, s -> s.category,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), s -> s.recipeItems,
                ItemStack.STREAM_CODEC, s -> s.recipeOutput,
                ByteBufCodecs.VAR_INT, s -> s.requiredLevel,
                ByteBufCodecs.VAR_INT, s -> s.requiredLava,
                ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.SKILL).apply(ByteBufCodecs.list()), s -> s.requiredSkills,
                ShapelessWeaponTableRecipe::new
        );

        @Override
        public @NotNull MapCodec<ShapelessWeaponTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ShapelessWeaponTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
