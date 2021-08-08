package de.teamlapen.vampirism.client.gui.recipebook;

import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.recipebook.ServerPlaceRecipe;

public class WeaponTableRecipePlacer<C extends Container> extends ServerPlaceRecipe<C> {
    public WeaponTableRecipePlacer(RecipeBookMenu<C> recipeBookContainer) {
        super(recipeBookContainer);
    }

    protected void clearGrid() {
        for (int i = 0; i < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++i) {
            if (i != this.menu.getResultSlotIndex() || !(this.menu instanceof WeaponTableContainer) && !(this.menu instanceof InventoryMenu)) {
                this.moveItemToInventory(i);
            }
        }

        this.menu.clearCraftingContent();
    }
}
