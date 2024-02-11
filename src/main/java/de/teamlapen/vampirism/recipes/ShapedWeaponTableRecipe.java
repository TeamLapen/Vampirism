package de.teamlapen.vampirism.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.FactionCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
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
public class ShapedWeaponTableRecipe implements CraftingRecipe, IWeaponTableRecipe, IShapedRecipe<CraftingContainer> {
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
    public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registryAccess) {
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

    @Override
    public int getRecipeHeight() {
        return getHeight();
    }

    @NotNull
    @Override
    public ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return this.recipeOutput;
    }

    @Override
    public int getRecipeWidth() {
        return getWidth();
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
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
        return this.pattern.matches(inv);
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return this.category;
    }

    public static class Serializer implements RecipeSerializer<ShapedWeaponTableRecipe> {

        public static final Codec<ShapedWeaponTableRecipe> CODEC = RecordCodecBuilder.create(inst -> {
            return inst.group(
                    ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(p_311729_ -> p_311729_.group),
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_311732_ -> p_311732_.category),
                    ShapedRecipePattern.MAP_CODEC.forGetter(p_311733_ -> p_311733_.pattern),
                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(p_311730_ -> p_311730_.recipeOutput),
                    ExtraCodecs.strictOptionalField(Codec.INT, "level", 1).forGetter(p -> p.requiredLevel),
                    ExtraCodecs.strictOptionalField(FactionCodec.<IHunterPlayer>skillCodec().listOf(), "skill", Collections.emptyList()).forGetter(p -> p.requiredSkills),
                    ExtraCodecs.strictOptionalField(Codec.INT, "lava", 0).forGetter(p -> p.requiredLava)
            ).apply(inst, ShapedWeaponTableRecipe::new);
        });

        @Override
        public @NotNull Codec<ShapedWeaponTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull ShapedWeaponTableRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return buffer.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ShapedWeaponTableRecipe recipe) {
            buffer.writeJsonWithCodec(CODEC, recipe);
        }
    }
}
