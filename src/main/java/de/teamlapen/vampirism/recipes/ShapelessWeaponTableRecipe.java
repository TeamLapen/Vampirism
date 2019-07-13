package de.teamlapen.vampirism.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

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
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * 
 * @author Cheaterpaul
 */
public class ShapelessWeaponTableRecipe implements ICraftingRecipe, IWeaponTableRecipe {
    protected static int MAX_WIDTH = 4;
    protected static int MAX_HEIGHT = 4;

    private final ResourceLocation id;
    private final String group;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack recipeOutput;
    private final int requiredLevel;
    private final ISkill[] requiredSkills;
    private final int requiredLava;
    private final boolean isSimple;

    public ShapelessWeaponTableRecipe(ResourceLocation recipeId, String group, NonNullList<Ingredient> ingredients, ItemStack result, int level, int lava, ISkill[] skills) {
        this.id = recipeId;
        this.group = group;
        this.recipeItems = ingredients;
        this.recipeOutput = result;
        this.requiredLevel = level;
        this.requiredLava = lava;
        this.requiredSkills = skills;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    private static ISkill[] deserializeSkills(JsonArray jsonObject) {
        if (jsonObject == null || jsonObject.size() == 0)
            return null;
        ISkill[] skills = new ISkill[jsonObject.size()];
        for (int i = 0; i < skills.length; ++i) {
            String s = JSONUtils.getString(jsonObject.get(i), "skill[" + i + "]");
            ISkill skill = ModRegistries.SKILLS.getValue(new ResourceLocation(s));
            if (skill == null) {
                throw new JsonSyntaxException("Unknown skill '" + s + "'");
            } else {
                skills[i] = skill;
            }
        }
        return skills;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= this.recipeItems.size();
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return recipeOutput;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    public int getRequiredLavaUnits() {
        return requiredLava;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    @Override
    public ISkill[] getRequiredSkills() {
        return requiredSkills;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPELESS_CRAFTING_WEAPONTABLE;
    }

    @Override
    public IRecipeType<? extends IRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < inv.getHeight(); ++j) {
            for (int k = 0; k < inv.getWidth(); ++k) {
                ItemStack itemstack = inv.getStackInSlot(k + j * inv.getWidth());
                if (!itemstack.isEmpty()) {
                    ++i;
                    if (isSimple)
                        recipeitemhelper.accountStack(new ItemStack(itemstack.getItem()));
                    else
                        inputs.add(itemstack);
                }
            }
        }

        return i == this.recipeItems.size() && (isSimple ? recipeitemhelper.canCraft(this, null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.recipeItems) != null);

    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessWeaponTableRecipe> {
        @Override
        public ShapelessWeaponTableRecipe read(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            int level = JSONUtils.getInt(json, "level", 1);
            ISkill[] skills = ShapelessWeaponTableRecipe.deserializeSkills(JSONUtils.getJsonArray(json, "skill", null));
            int lava = JSONUtils.getInt(json, "lava", 0);
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (ingredients.size() > ShapelessWeaponTableRecipe.MAX_WIDTH * ShapelessWeaponTableRecipe.MAX_HEIGHT) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + (ShapelessWeaponTableRecipe.MAX_WIDTH * ShapelessWeaponTableRecipe.MAX_HEIGHT));
            } else {
                ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
                return new ShapelessWeaponTableRecipe(recipeId, group, ingredients, result, level, lava, skills);
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for (int i = 0; i < p_199568_0_.size(); ++i) {
                Ingredient ingredient = Ingredient.deserialize(p_199568_0_.get(i));
                if (!ingredient.hasNoMatchingItems()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        @Override
        public ShapelessWeaponTableRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readString(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(buffer.readVarInt(), Ingredient.EMPTY);
            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.read(buffer));
            }
            ItemStack result = buffer.readItemStack();
            int level = buffer.readVarInt();
            int lava = buffer.readVarInt();
            ISkill[] skills = new ISkill[buffer.readVarInt()];
            for (int i = 0; i < skills.length; i++) {
                skills[i] = ModRegistries.SKILLS.getValue(new ResourceLocation(buffer.readString(32767)));
            }
            return new ShapelessWeaponTableRecipe(recipeId, group, ingredients, result, level, lava, skills);
        }

        @Override
        public void write(PacketBuffer buffer, ShapelessWeaponTableRecipe recipe) {
            buffer.writeString(recipe.group);
            buffer.writeVarInt(recipe.recipeItems.size());

            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.write(buffer);
            }
            buffer.writeItemStack(recipe.recipeOutput);
            buffer.writeVarInt(recipe.requiredLevel);
            buffer.writeVarInt(recipe.requiredLava);
            buffer.writeVarInt(recipe.requiredSkills.length);
            for (ISkill skill : recipe.requiredSkills) {
                buffer.writeString(skill.getRegistryName().toString());
            }
        }

    }
}
