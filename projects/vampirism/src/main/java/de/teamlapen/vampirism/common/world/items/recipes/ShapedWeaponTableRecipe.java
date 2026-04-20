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
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Cheaterpaul
 */
public class ShapedWeaponTableRecipe implements IWeaponTableRecipe {

    private final String group;
    private final ShapedRecipePattern pattern;
    private final ItemStack recipeOutput;
    private final int requiredLevel;
    private final List<Holder<ISkill<?>>> requiredSkills;
    private final int requiredLava;
    @Nullable
    private PlacementInfo placementInfo;

    public ShapedWeaponTableRecipe(String group, ShapedRecipePattern pattern, ItemStack recipeOutputIn, int requiredLevel, List<Holder<ISkill<?>>> requiredSkills, int requiredLava) {
        this.group = group;
        this.pattern = pattern;
        this.recipeOutput = recipeOutputIn;
        this.requiredLevel = requiredLevel;
        this.requiredSkills = requiredSkills;
        this.requiredLava = requiredLava;
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
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return this.recipeOutput.copy();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(this.pattern.ingredients());
        }
        return this.placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapedCraftingRecipeDisplay(
                this.pattern.width(),
                this.pattern.height(),
                this.pattern.ingredients().stream()
                        .map(x -> x.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE))
                        .toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.recipeOutput),
                new SlotDisplay.ItemSlotDisplay(ModBlocks.WEAPON_TABLE.asItem())
        ));
    }

    @Override
    public List<Ingredient> getIngredients() {
        return pattern.ingredients().stream().flatMap(Optional::stream).toList();
    }

    @Override
    public RecipeSerializer<? extends ShapedWeaponTableRecipe> getSerializer() {
        return ModRecipes.SHAPED_CRAFTING_WEAPONTABLE.get();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    public static class Serializer implements RecipeSerializer<ShapedWeaponTableRecipe> {

        public static final MapCodec<ShapedWeaponTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.recipeOutput),
                Codec.INT.optionalFieldOf("level", 1).forGetter(recipe -> recipe.requiredLevel),
                ModRegistries.SKILLS.holderByNameCodec().listOf().optionalFieldOf("skill", Collections.emptyList()).forGetter(recipe -> recipe.requiredSkills),
                Codec.INT.optionalFieldOf("lava", 0).forGetter(recipe -> recipe.requiredLava)
        ).apply(instance, ShapedWeaponTableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedWeaponTableRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, recipe -> recipe.group,
                ShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
                ItemStack.STREAM_CODEC, recipe -> recipe.recipeOutput,
                ByteBufCodecs.VAR_INT, recipe -> recipe.requiredLevel,
                ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL).apply(ByteBufCodecs.list()), recipe -> recipe.requiredSkills,
                ByteBufCodecs.VAR_INT, recipe -> recipe.requiredLava,
                ShapedWeaponTableRecipe::new
        );

        @Override
        public MapCodec<ShapedWeaponTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedWeaponTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
