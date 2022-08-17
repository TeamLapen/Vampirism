package de.teamlapen.vampirism.inventory.recipes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

class VampirismRecipeHelper {

    @NotNull
    static ISkill<?> @NotNull [] deserializeSkills(@Nullable JsonArray jsonObject) {
        if (jsonObject == null || jsonObject.size() == 0)
            return new ISkill[0];
        ISkill<?>[] skills = new ISkill[jsonObject.size()];
        for (int i = 0; i < skills.length; ++i) {
            String s = GsonHelper.convertToString(jsonObject.get(i), "skill[" + i + "]");
            ISkill<?> skill = RegUtil.getSkill(new ResourceLocation(s));
            if (skill == null) {
                throw new JsonSyntaxException("Unknown skill '" + s + "'");
            } else {
                skills[i] = skill;
            }
        }
        return skills;
    }

    static String @NotNull [] shrink(String @NotNull ... toShrink) {
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

    private static int firstNonSpace(@NotNull String str) {
        int i;
        for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(@NotNull String str) {
        int i;
        for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
        }

        return i;
    }

    /**
     * deserialize ingredients for shapeless recipes
     */
    static @NotNull NonNullList<Ingredient> readIngredients(@NotNull JsonArray ingredientArray) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for (int i = 0; i < ingredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
            if (!ingredient.isEmpty()) {
                nonnulllist.add(ingredient);
            }
        }

        return nonnulllist;
    }

    static @NotNull FluidStack deserializeFluid(@NotNull JsonObject object) {
        String s = GsonHelper.getAsString(object, "fluid");
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(s));
        if (fluid == null) throw new JsonSyntaxException("Unknown fluid '" + s + "'");
        if (object.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = GsonHelper.getAsInt(object, "amount", 1);
            return new FluidStack(fluid, i);
        }
    }

    /**
     * Returns a key json object as a Java HashMap.
     */
    static @NotNull Map<String, Ingredient> deserializeKey(@NotNull JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    /**
     * deserialize ingredients for shaped recipes
     */
    static @NotNull NonNullList<Ingredient> deserializeIngredients(String @NotNull [] pattern, @NotNull Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
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

    /**
     * get pattern from shaped recipe
     *
     * @param max crafting grid max size (4x4 -> 4)
     */
    static String @NotNull [] patternFromJson(@NotNull JsonArray jsonArr, int max) {
        String[] astring = new String[jsonArr.size()];
        if (astring.length > max) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + max + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = GsonHelper.convertToString(jsonArr.get(i), "pattern[" + i + "]");
                if (s.length() > max) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + max + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    static @NotNull Either<Ingredient, FluidStack> getFluidOrItem(@NotNull JsonObject json) {
        if (json.has("fluidItem")) {
            return Either.left(Ingredient.fromJson(json.get("fluidItem")));

        } else {
            return Either.right(deserializeFluid(json.getAsJsonObject("fluid")));

        }
    }
}
