package de.teamlapen.vampirism.common.integration.jei.recipes.maker;

import de.teamlapen.vampirism.api.world.items.IBlessableItem;
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
                    if (stack.getItem() instanceof IBlessableItem item && item.canBeBlessed()) {
                        //noinspection DataFlowIssue
                        consumer.accept(new BlessableRecipe(item, item.getBlessedItem(), item.requiredSkill()));
                    }
                }).toList();
    }
}
