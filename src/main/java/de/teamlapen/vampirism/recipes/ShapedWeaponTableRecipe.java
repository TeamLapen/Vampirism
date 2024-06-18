package de.teamlapen.vampirism.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.FactionCodec;
import de.teamlapen.vampirism.util.StreamCodecExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author Cheaterpaul
 */
public class ShapedWeaponTableRecipe implements Recipe<CraftingInput>, IWeaponTableRecipe, IShapedRecipe<CraftingInput> {
    protected final static int MAX_WIDTH = 4;
    protected final static int MAX_HEIGHT = 4;

    private final CraftingBookCategory category;
    private final String group;
    private final ShapedRecipePattern pattern;
    private final ItemStack recipeOutput;
    private final int requiredLevel;
    @NotNull
    private final List<ISkill<IHunterPlayer>> requiredSkills;
    private final int requiredLava;

    public ShapedWeaponTableRecipe(String groupIn, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack recipeOutputIn,int requiredLevel, @NotNull List<ISkill<IHunterPlayer>> requiredSkills, int requiredLava) {
        this.category = category;
        this.group = groupIn;
        this.pattern = pattern;
        this.recipeOutput = recipeOutputIn;
        this.requiredLevel = requiredLevel;
        this.requiredSkills = requiredSkills;
        this.requiredLava = requiredLava;
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingInput inv, @NotNull HolderLookup.Provider registryAccess) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= this.pattern.width() && pHeight >= this.pattern.height();
    }

    @NotNull
    public String getGroup() {
        return this.group;
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @NotNull
    @Override
    public ItemStack getResultItem(@NotNull HolderLookup.Provider registryAccess) {
        return this.recipeOutput;
    }

    public int getRequiredLavaUnits() {
        return requiredLava;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    @NotNull
    @Override
    public List<ISkill<IHunterPlayer>> getRequiredSkills() {
        return requiredSkills;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPED_CRAFTING_WEAPONTABLE.get();
    }

    @NotNull
    @Override
    public RecipeType<IWeaponTableRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get();
    }

    @Override
    public boolean matches(@NotNull CraftingInput inv, @NotNull Level worldIn) {
        return this.pattern.matches(inv);
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.pattern.ingredients();
    }

    public static class Serializer implements RecipeSerializer<ShapedWeaponTableRecipe> {

        public static final MapCodec<ShapedWeaponTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(
                    Codec.STRING.optionalFieldOf( "group", "").forGetter(p_311729_ -> p_311729_.group),
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_311732_ -> p_311732_.category),
                    ShapedRecipePattern.MAP_CODEC.forGetter(p_311733_ -> p_311733_.pattern),
                    ItemStack.CODEC.fieldOf("result").forGetter(p_311730_ -> p_311730_.recipeOutput),
                    Codec.INT.optionalFieldOf( "level", 1).forGetter(p -> p.requiredLevel),
                    FactionCodec.<IHunterPlayer>skillCodec().listOf().optionalFieldOf( "skill", Collections.emptyList()).forGetter(p -> p.requiredSkills),
                    Codec.INT.optionalFieldOf( "lava", 0).forGetter(p -> p.requiredLava)
            ).apply(inst, ShapedWeaponTableRecipe::new);
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedWeaponTableRecipe> STREAM_CODEC = StreamCodecExtension.composite(
                ByteBufCodecs.STRING_UTF8, s -> s.group,
                CraftingBookCategory.STREAM_CODEC, s -> s.category,
                ShapedRecipePattern.STREAM_CODEC, s -> s.pattern,
                ItemStack.STREAM_CODEC, s -> s.recipeOutput,
                ByteBufCodecs.VAR_INT, s -> s.requiredLevel,
                FactionCodec.<RegistryFriendlyByteBuf,IHunterPlayer>skillStreamCodec().apply(ByteBufCodecs.list()), s -> s.requiredSkills,
                ByteBufCodecs.VAR_INT, s -> s.requiredLava,
                ShapedWeaponTableRecipe::new
        );

        @Override
        public @NotNull MapCodec<ShapedWeaponTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ShapedWeaponTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
