package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShapelessWeaponTableRecipe implements IWeaponTableRecipe {

    private final String group;
    private final List<Ingredient> ingredients;
    private final ItemStack recipeOutput;
    private final int requiredLevel;
    private final List<Holder<ISkill<?>>> requiredSkills;
    private final int requiredLava;
    private final boolean isSimple;
    @Nullable
    private PlacementInfo placementInfo;

    public ShapelessWeaponTableRecipe(String group, List<Ingredient> ingredients, ItemStack result, int requiredLevel, int requiredLava, List<Holder<ISkill<?>>> requiredSkills) {
        this.group = group;
        this.ingredients = ingredients;
        this.recipeOutput = result;
        this.requiredLevel = requiredLevel;
        this.requiredLava = requiredLava;
        this.requiredSkills = requiredSkills;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public int getRequiredLavaUnits() {
        return requiredLava;
    }

    @Override
    public int getRequiredLevel() {
        return requiredLevel;
    }

    @Override
    public List<Holder<ISkill<?>>> getRequiredSkills() {
        return requiredSkills;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != this.ingredients.size()) {
            return false;
        } else if (!isSimple) {
            ArrayList<ItemStack> nonEmptyItems = new ArrayList<>(input.ingredientCount());
            for (var item : input.items()) {
                if (!item.isEmpty()) {
                    nonEmptyItems.add(item);
                }
            }
            return RecipeMatcher.findMatches(nonEmptyItems, this.ingredients) != null;
        } else {
            return input.size() == 1 && this.ingredients.size() == 1
                    ? this.ingredients.getFirst().test(input.getItem(0))
                    : input.stackedContents().canCraft(this, null);
        }
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return this.recipeOutput.copy();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(this.ingredients);
        }
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessCraftingRecipeDisplay(
                this.ingredients.stream().map(Ingredient::display).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.recipeOutput),
                new SlotDisplay.ItemSlotDisplay(ModBlocks.WEAPON_TABLE.asItem())
        ));
    }

    @Override
    public RecipeSerializer<? extends ShapelessWeaponTableRecipe> getSerializer() {
        return ModRecipes.SHAPELESS_CRAFTING_WEAPONTABLE.get();
    }

    public static class Serializer implements RecipeSerializer<ShapelessWeaponTableRecipe> {

        private static final MapCodec<ShapelessWeaponTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessWeaponTableRecipe::group),
                Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, 16)).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.recipeOutput),
                Codec.INT.optionalFieldOf("level", 1).forGetter(recipe -> recipe.requiredLevel),
                Codec.INT.optionalFieldOf("lava", 0).forGetter(recipe -> recipe.requiredLava),
                ModRegistries.SKILLS.holderByNameCodec().listOf().optionalFieldOf("skill", Collections.emptyList()).forGetter(recipe -> recipe.requiredSkills)
        ).apply(instance, ShapelessWeaponTableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessWeaponTableRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, recipe -> recipe.group,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), recipe -> recipe.ingredients,
                ItemStack.STREAM_CODEC, recipe -> recipe.recipeOutput,
                ByteBufCodecs.VAR_INT, recipe -> recipe.requiredLevel,
                ByteBufCodecs.VAR_INT, recipe -> recipe.requiredLava,
                ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL).apply(ByteBufCodecs.list()), recipe -> recipe.requiredSkills,
                ShapelessWeaponTableRecipe::new
        );

        @Override
        public MapCodec<ShapelessWeaponTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapelessWeaponTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
