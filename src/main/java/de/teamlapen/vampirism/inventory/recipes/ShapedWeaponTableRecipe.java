package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Cheaterpaul
 */
public class ShapedWeaponTableRecipe implements ICraftingRecipe, IWeaponTableRecipe, IShapedRecipe<CraftingInventory> {
    protected static int MAX_WIDTH = 4;
    protected static int MAX_HEIGHT = 4;

    private final ResourceLocation id;
    private final String group;
    private final int recipeWidth;
    private final int recipeHeight;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack recipeOutput;
    private final int requiredLevel;
    private final @Nonnull
    ISkill[] requiredSkills;
    private final int requiredLava;

    public ShapedWeaponTableRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, int requiredLevel, @Nonnull ISkill[] requiredSkills, int requiredLava) {
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

    @Override
    public boolean canFit(int width, int height) {
        return width >= this.recipeWidth && height >= this.recipeHeight;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return this.recipeOutput.copy();
    }

    public String getGroup() {
        return this.group;
    }

    public int getHeight() {
        return this.recipeHeight;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public int getRecipeHeight() {
        return getHeight();
    }

    @Override
    public ItemStack getRecipeOutput() {
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
    public ISkill[] getRequiredSkills() {
        return requiredSkills;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.shaped_crafting_weapontable;
    }

    @Override
    public IRecipeType<? extends IRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE;
    }

    public int getWidth() {
        return this.recipeWidth;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
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
    private boolean checkMatch(CraftingInventory craftingInventory, int startRow, int startColumn, boolean p_77573_4_) {
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

                if (!ingredient.test(craftingInventory.getStackInSlot(i + j * craftingInventory.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedWeaponTableRecipe> {
        @Override
        public ShapedWeaponTableRecipe read(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> map = VampirismRecipeHelper.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] astring = VampirismRecipeHelper.shrink(VampirismRecipeHelper.patternFromJson(JSONUtils.getJsonArray(json, "pattern"), MAX_HEIGHT));
            int width = astring[0].length();
            int length = astring.length;
            NonNullList<Ingredient> ingredients = VampirismRecipeHelper.deserializeIngredients(astring, map, width, length);
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), true);
            int level = JSONUtils.getInt(json, "level", 1);
            ISkill[] skill = VampirismRecipeHelper.deserializeSkills(JSONUtils.getJsonArray(json, "skill", null));
            int lava = JSONUtils.getInt(json, "lava", 0);

            return new ShapedWeaponTableRecipe(recipeId, group, width, length, ingredients, result, level, skill, lava);
        }

        @Override
        public ShapedWeaponTableRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readString(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(height * width, Ingredient.EMPTY);
            for (int k = 0; k < ingredients.size(); ++k) {
                ingredients.set(k, Ingredient.read(buffer));
            }
            ItemStack itemstack = buffer.readItemStack();
            int level = buffer.readVarInt();
            int lava = buffer.readVarInt();
            ISkill[] skills = new ISkill[buffer.readVarInt()];
            if (skills.length != 0) {
                for (int i = 0; i < skills.length; i++) {
                    skills[i] = ModRegistries.SKILLS.getValue(new ResourceLocation(buffer.readString(32767)));
                }
            }
            return new ShapedWeaponTableRecipe(recipeId, group, width, height, ingredients, itemstack, level, skills, lava);
        }

        @Override
        public void write(PacketBuffer buffer, ShapedWeaponTableRecipe recipe) {
            buffer.writeVarInt(recipe.recipeWidth);
            buffer.writeVarInt(recipe.recipeHeight);
            buffer.writeString(recipe.group);
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.write(buffer);
            }
            buffer.writeItemStack(recipe.recipeOutput);
            buffer.writeVarInt(recipe.requiredLevel);
            buffer.writeVarInt(recipe.requiredLava);
            buffer.writeVarInt(recipe.requiredSkills.length);
            if (recipe.requiredSkills.length != 0) {
                for (ISkill skill : recipe.requiredSkills) {
                    buffer.writeString(skill.getRegistryName().toString());
                }
            }
        }

    }
}
