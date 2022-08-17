package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class ShapelessWeaponTableRecipe implements CraftingRecipe, IWeaponTableRecipe {
    protected static final int MAX_WIDTH = 4;
    protected static final int MAX_HEIGHT = 4;

    private final ResourceLocation id;
    private final String group;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack recipeOutput;
    private final int requiredLevel;
    private final ISkill<IHunterPlayer>[] requiredSkills;
    private final int requiredLava;
    private final boolean isSimple;

    public ShapelessWeaponTableRecipe(@NotNull ResourceLocation recipeId,@NotNull String group,@NotNull NonNullList<Ingredient> ingredients,@NotNull ItemStack result, int level, int lava, @NotNull ISkill<IHunterPlayer>[] skills) {
        this.id = recipeId;
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
    public ItemStack assemble(@NotNull CraftingContainer inv) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.recipeItems.size();
    }

    @NotNull
    @Override
    public String getGroup() {
        return group;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @NotNull
    @Override
    public ItemStack getResultItem() {
        return recipeOutput;
    }

    public int getRequiredLavaUnits() {
        return requiredLava;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    @NotNull
    @Override
    public ISkill<IHunterPlayer>[] getRequiredSkills() {
        return requiredSkills;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPELESS_CRAFTING_WEAPONTABLE.get();
    }

    @NotNull
    @Override
    public RecipeType<IWeaponTableRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get();
    }

    @Override
    public boolean matches(@NotNull CraftingContainer inv,@NotNull Level worldIn) {
        StackedContents recipeitemhelper = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < inv.getHeight(); ++j) {
            for (int k = 0; k < inv.getWidth(); ++k) {
                ItemStack itemstack = inv.getItem(k + j * inv.getWidth());
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

    public static class Serializer implements RecipeSerializer<ShapelessWeaponTableRecipe> {
        @NotNull
        @Override
        public ShapelessWeaponTableRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            NonNullList<Ingredient> ingredients = VampirismRecipeHelper.readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            int level = GsonHelper.getAsInt(json, "level", 1);
            ISkill<?>[] skills = VampirismRecipeHelper.deserializeSkills(GsonHelper.getAsJsonArray(json, "skill", null));
            int lava = GsonHelper.getAsInt(json, "lava", 0);
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (ingredients.size() > ShapelessWeaponTableRecipe.MAX_WIDTH * ShapelessWeaponTableRecipe.MAX_HEIGHT) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + (ShapelessWeaponTableRecipe.MAX_WIDTH * ShapelessWeaponTableRecipe.MAX_HEIGHT));
            } else {
                ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                //noinspection unchecked
                return new ShapelessWeaponTableRecipe(recipeId, group, ingredients, result, level, lava, (ISkill<IHunterPlayer>[]) skills);
            }
        }

        @Override
        public ShapelessWeaponTableRecipe fromNetwork(@NotNull ResourceLocation recipeId,@NotNull FriendlyByteBuf buffer) {
            String group = buffer.readUtf(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(buffer.readVarInt(), Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buffer));
            ItemStack result = buffer.readItem();
            int level = buffer.readVarInt();
            int lava = buffer.readVarInt();
            ISkill<?>[] skills = new ISkill[buffer.readVarInt()];
            for (int i = 0; i < skills.length; i++) {
                skills[i] = RegUtil.getSkill(new ResourceLocation(buffer.readUtf(32767)));
            }
            //noinspection unchecked
            return new ShapelessWeaponTableRecipe(recipeId, group, ingredients, result, level, lava, (ISkill<IHunterPlayer>[]) skills);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer,@NotNull  ShapelessWeaponTableRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.recipeItems.size());
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.recipeOutput);
            buffer.writeVarInt(recipe.requiredLevel);
            buffer.writeVarInt(recipe.requiredLava);
            buffer.writeVarInt(recipe.requiredSkills.length);
            for (ISkill<?> skill : recipe.requiredSkills) {
                buffer.writeUtf(RegUtil.id(skill) .toString());
            }
        }

    }
}
