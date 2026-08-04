package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.util.serialization.StreamCodecExtension;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
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

    private final CraftingRecipe.CraftingBookInfo category;
    private final CommonInfo commonInfo;
    private final List<Ingredient> ingredients;
    private final ItemStackTemplate recipeOutput;
    private final int requiredLevel;
    private final List<Holder<? extends ISkill<?>>> requiredSkills;
    private final int requiredLava;
    private final boolean isSimple;
    @Nullable
    private PlacementInfo placementInfo;

    public ShapelessWeaponTableRecipe(CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo category, List<Ingredient> ingredients, ItemStackTemplate result, int requiredLevel, int requiredLava, List<Holder<? extends ISkill<?>>> requiredSkills) {
        this.category = category;
        this.commonInfo = commonInfo;
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
    public boolean showNotification() {
        return this.commonInfo.showNotification();
    }

    public String group() {
        return this.category.group();
    }

    @Override
    public List<Holder<? extends ISkill<?>>> getRequiredSkills() {
        return requiredSkills;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return this.ingredients;
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
    public ItemStack assemble(CraftingInput input) {
        return this.recipeOutput.create();
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

    public static final MapCodec<ShapelessWeaponTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                CommonInfo.MAP_CODEC.forGetter(s -> s.commonInfo),
                CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(p_301133_ -> p_301133_.category),
                Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, 16)).fieldOf("ingredients").forGetter(x -> x.ingredients),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(p_301142_ -> p_301142_.recipeOutput),
                Codec.INT.optionalFieldOf("level", 1).forGetter(p -> p.requiredLevel),
                Codec.INT.optionalFieldOf("lava", 0).forGetter(p -> p.requiredLava),
                ISkill.CODEC.listOf().optionalFieldOf("skill", Collections.emptyList()).forGetter(p -> p.requiredSkills)
        ).apply(inst, ShapelessWeaponTableRecipe::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessWeaponTableRecipe> STREAM_CODEC = StreamCodecExtension.composite(
            CommonInfo.STREAM_CODEC, s -> s.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC, s -> s.category,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), s -> s.ingredients,
            ItemStackTemplate.STREAM_CODEC, s -> s.recipeOutput,
            ByteBufCodecs.VAR_INT, s -> s.requiredLevel,
            ByteBufCodecs.VAR_INT, s -> s.requiredLava,
            SafeCast.<StreamCodec<RegistryFriendlyByteBuf, Holder<? extends ISkill<?>>>>cast(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL)).apply(ByteBufCodecs.list()), s -> s.requiredSkills,
            ShapelessWeaponTableRecipe::new
    );
}
