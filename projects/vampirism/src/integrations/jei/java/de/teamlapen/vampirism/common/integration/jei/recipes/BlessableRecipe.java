package de.teamlapen.vampirism.common.integration.jei.recipes;

import de.teamlapen.vampirism.common.world.items.BlessableItem;
import net.minecraft.world.item.Item;

public record BlessableRecipe(boolean enhanced, BlessableItem input, Item output) {
}
