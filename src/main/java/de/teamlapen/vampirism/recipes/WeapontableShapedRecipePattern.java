package de.teamlapen.vampirism.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record WeapontableShapedRecipePattern(int width, int height, NonNullList<Ingredient> ingredients, Optional<Data> data) {
    public static final int MAX_WIDTH = 4;
    public static final int MAX_HEIGHT = 4;

    public static void setCraftingSize(int width, int height) {
        
    }

    public static record Data(Map<Character, Ingredient> key, List<String> pattern) {

    }
}
