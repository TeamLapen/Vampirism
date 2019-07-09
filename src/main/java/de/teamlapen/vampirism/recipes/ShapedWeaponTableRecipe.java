package de.teamlapen.vampirism.recipes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.VampirismRegistries;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author Cheaterpaul
 */
public class ShapedWeaponTableRecipe implements IWeaponTableRecipe, net.minecraftforge.common.crafting.IShapedRecipe {
    protected static int MAX_WIDTH = 4;
    protected static int MAX_HEIGHT = 4;

    private final ResourceLocation id;
    private final String group;
    private final int recipeWidth;
    private final int recipeHeight;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack recipeOutput;
    private final int requiredLevel;
    private final ISkill[] requiredSkills;
    private final int requiredLava;

    public ShapedWeaponTableRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, int requiredLevel, ISkill[] requiredSkills, int requiredLava) {
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
    public boolean matches(IInventory inv, World worldIn) {
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
    private boolean checkMatch(IInventory craftingInventory, int startRow, int startColumn, boolean p_77573_4_) {
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

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= this.recipeWidth && height >= this.recipeHeight;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPED_CRAFTING_WEAPONTABLE;
    }

    public String getGroup() {
        return this.group;
    }

    public int getWidth() {
        return this.recipeWidth;
    }

    @Override
    public int getRecipeWidth() {
        return getWidth();
    }

    public int getHeight() {
        return this.recipeHeight;
    }

    @Override
    public int getRecipeHeight() {
        return getHeight();
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getRequiredLavaUnits() {
        return requiredLava;
    }

    @Override
    public ISkill[] getRequiredSkills() {
        return requiredSkills;
    }

    @Override
    public RecipeType<? extends IRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE;
    }

    private static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(keys.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + patternWidth * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    private static String[] shrink(String... toShrink) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < toShrink.length; ++i1) {
            String s = toShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (toShrink.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[toShrink.length - l - k];

            for (int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = toShrink[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    private static int firstNonSpace(String str) {
        int i;
        for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(String str) {
        int i;
        for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
        }

        return i;
    }

    private static String[] patternFromJson(JsonArray jsonArr) {
        String[] astring = new String[jsonArr.size()];
        if (astring.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = JSONUtils.getString(jsonArr.get(i), "pattern[" + i + "]");
                if (s.length() > MAX_WIDTH) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    /**
     * Returns a key json object as a Java HashMap.
     */
    private static Map<String, Ingredient> deserializeKey(JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    private static ItemStack deserializeItem(JsonObject p_199798_0_) {
        String s = JSONUtils.getString(p_199798_0_, "item");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
        if (item == null) {
            throw new JsonSyntaxException("Unknown item '" + s + "'");
        } else if (p_199798_0_.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = JSONUtils.getInt(p_199798_0_, "count", 1);
            return new ItemStack(item, i);
        }
    }

    private static ISkill[] deserializeSkills(JsonArray jsonObject) {
        if (jsonObject == null || jsonObject.size() == 0)
            return null;
        ISkill[] skills = new ISkill[jsonObject.size()];
        for (int i = 0; i < skills.length; ++i) {
            String s = JSONUtils.getString(jsonObject.get(i), "skill[" + i + "]");
            ISkill skill = VampirismRegistries.SKILLS.getValue(new ResourceLocation(s));
            if (skill == null) {
                throw new JsonSyntaxException("Unknown skill '" + s + "'");
            } else {
                skills[i] = skill;
            }
        }
        return skills;
    }

    public static class Serializer implements IRecipeSerializer<ShapedWeaponTableRecipe> {
        public Serializer() {
        }

        private static final ResourceLocation NAME = new ResourceLocation(REFERENCE.MODID, "shaped_weapon_table_recipe");
        @Override
        public ShapedWeaponTableRecipe read(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> map = ShapedWeaponTableRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] astring = ShapedWeaponTableRecipe.shrink(ShapedWeaponTableRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
            int height = astring[0].length();
            int length = astring.length;
            NonNullList<Ingredient> ingredients = ShapedWeaponTableRecipe.deserializeIngredients(astring, map, height, length);
            ItemStack result = ShapedWeaponTableRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            int level = JSONUtils.getInt(json, "level", 1);
            ISkill[] skill = ShapedWeaponTableRecipe.deserializeSkills(JSONUtils.getJsonArray(json, "skill", null));
            int lava = JSONUtils.getInt(json, "lava", 0);

            return new ShapedWeaponTableRecipe(recipeId, group, height, length, ingredients, result, level, skill, lava);
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
                    skills[i] = VampirismRegistries.SKILLS.getValue(new ResourceLocation(buffer.readString(32767)));
                }
            }
            return new ShapedWeaponTableRecipe(recipeId, group, height, width, ingredients, itemstack, level, skills, lava);
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

        @Override
        public ResourceLocation getName() {
            return NAME;
        }

    }
}
