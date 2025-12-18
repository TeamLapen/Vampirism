package de.teamlapen.vampirism.common.integration.jei.recipes.maker;

import de.teamlapen.vampirism.common.integration.jei.recipes.BlessableRecipe;
import de.teamlapen.vampirism.common.world.items.BlessableItem;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class BlessableRecipeMaker {

    public static List<BlessableRecipe> getRecipes(IIngredientManager ingredientManager) {
        return ingredientManager.getAllItemStacks().stream()
                .mapMulti((ItemStack stack, Consumer<BlessableRecipe> consumer) -> {
                    if (stack.getItem() instanceof BlessableItem item) {
                        consumer.accept(new BlessableRecipe(false, item, item.getBlessedItem()));
                        if (item.getEnhancedBlessedItem() instanceof Item enhancedItem) {
                            consumer.accept(new BlessableRecipe(true, item, enhancedItem));
                        }
                    }
                }).toList();
    }
}
