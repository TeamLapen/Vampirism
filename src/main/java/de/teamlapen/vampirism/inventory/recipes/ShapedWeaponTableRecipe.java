package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Cheaterpaul
 */
public class ShapedWeaponTableRecipe implements CraftingRecipe, IWeaponTableRecipe, IShapedRecipe<CraftingContainer> {
    protected static int MAX_WIDTH = 4;
    protected static int MAX_HEIGHT = 4;

    private final ResourceLocation id;
    private final String group;
    private final int recipeWidth;
    private final int recipeHeight;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack recipeOutput;
    private final int requiredLevel;
    @Nonnull
    private final ISkill<IHunterPlayer>[] requiredSkills;
    private final int requiredLava;

    public ShapedWeaponTableRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, int requiredLevel, @Nonnull ISkill<IHunterPlayer>[] requiredSkills, int requiredLava) {
        this.id = idIn;
        this.group = groupIn;
        this.recipeWidth = recipeWidthIn;
        this.recipeHeight = recipeHeightIn;
        this.recipeItems = recipeItemsIn;
        this.recipeOutput = recipeOutputIn;
        this.requiredLevel = requiredLevel;
        this.requiredSkills = requiredSkills;
        this.requiredLava = requiredLava;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer inv) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.recipeWidth && height >= this.recipeHeight;
    }

    @Nonnull
    public String getGroup() {
        return this.group;
    }

    public int getHeight() {
        return this.recipeHeight;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public int getRecipeHeight() {
        return getHeight();
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
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

    @Nonnull
    @Override
    public ISkill<IHunterPlayer>[] getRequiredSkills() {
        return requiredSkills;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.shaped_crafting_weapontable.get();
    }

    @Nonnull
    @Override
    public RecipeType<IWeaponTableRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get();
    }

    public int getWidth() {
        return this.recipeWidth;
    }

    @Override
    public boolean matches(CraftingContainer inv, @Nonnull Level worldIn) {
        for (int i = 0; i <= inv.getWidth() - this.recipeWidth; ++i) {
            for (int j = 0; j <= inv.getHeight() - this.recipeHeight; ++j) {
                if (this.checkMatch(inv, i, j, true)) {
                    return true;
                }

                if (this.checkMatch(inv, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(CraftingContainer craftingInventory, int startRow, int startColumn, boolean p_77573_4_) {
        for (int i = 0; i < craftingInventory.getWidth(); ++i) {
            for (int j = 0; j < craftingInventory.getHeight(); ++j) {
                int k = i - startRow;
                int l = j - startColumn;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
                    if (p_77573_4_) {
                        ingredient = this.recipeItems.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
                    } else {
                        ingredient = this.recipeItems.get(k + l * this.recipeWidth);
                    }
                }

                if (!ingredient.test(craftingInventory.getItem(i + j * craftingInventory.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ShapedWeaponTableRecipe> {
        @Nonnull
        @Override
        public ShapedWeaponTableRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            Map<String, Ingredient> map = VampirismRecipeHelper.deserializeKey(GsonHelper.getAsJsonObject(json, "key"));
            String[] astring = VampirismRecipeHelper.shrink(VampirismRecipeHelper.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern"), MAX_HEIGHT));
            int width = astring[0].length();
            int length = astring.length;
            NonNullList<Ingredient> ingredients = VampirismRecipeHelper.deserializeIngredients(astring, map, width, length);
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            int level = GsonHelper.getAsInt(json, "level", 1);
            ISkill<?>[] skill = VampirismRecipeHelper.deserializeSkills(GsonHelper.getAsJsonArray(json, "skill", null));
            int lava = GsonHelper.getAsInt(json, "lava", 0);

            //noinspection unchecked
            return new ShapedWeaponTableRecipe(recipeId, group, width, length, ingredients, result, level, (ISkill<IHunterPlayer>[]) skill, lava);
        }

        @Override
        public ShapedWeaponTableRecipe fromNetwork(@Nonnull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readUtf(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(height * width, Ingredient.EMPTY);
            for (int k = 0; k < ingredients.size(); ++k) {
                ingredients.set(k, Ingredient.fromNetwork(buffer));
            }
            ItemStack itemstack = buffer.readItem();
            int level = buffer.readVarInt();
            int lava = buffer.readVarInt();
            ISkill<?>[] skills = new ISkill[buffer.readVarInt()];
            if (skills.length != 0) {
                for (int i = 0; i < skills.length; i++) {
                    skills[i] = ModRegistries.SKILLS.getValue(new ResourceLocation(buffer.readUtf(32767)));
                }
            }
            //noinspection unchecked
            return new ShapedWeaponTableRecipe(recipeId, group, width, height, ingredients, itemstack, level, (ISkill<IHunterPlayer>[]) skills, lava);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedWeaponTableRecipe recipe) {
            buffer.writeVarInt(recipe.recipeWidth);
            buffer.writeVarInt(recipe.recipeHeight);
            buffer.writeUtf(recipe.group);
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.recipeOutput);
            buffer.writeVarInt(recipe.requiredLevel);
            buffer.writeVarInt(recipe.requiredLava);
            buffer.writeVarInt(recipe.requiredSkills.length);
            if (recipe.requiredSkills.length != 0) {
                for (ISkill<?> skill : recipe.requiredSkills) {
                    buffer.writeUtf(skill.getRegistryName().toString());
                }
            }
        }

    }
}
