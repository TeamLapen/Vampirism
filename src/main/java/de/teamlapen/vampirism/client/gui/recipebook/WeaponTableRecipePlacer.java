package de.teamlapen.vampirism.client.gui.recipebook;

import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.crafting.ServerRecipePlacer;

public class WeaponTableRecipePlacer<C extends IInventory> extends ServerRecipePlacer<C> {
    public WeaponTableRecipePlacer(RecipeBookContainer<C> recipeBookContainer) {
        super(recipeBookContainer);
    }

    @Override
    protected void clearGrid() {
        for (int i = 0; i < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++i) {
            if (i != this.menu.getResultSlotIndex() || !(this.menu instanceof WeaponTableContainer) && !(this.menu instanceof PlayerContainer)) {
                this.moveItemToInventory(i);
            }
        }

        this.menu.clearCraftingContent();
    }
}
