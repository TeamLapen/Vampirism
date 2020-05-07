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

    protected void clear() {
        for (int i = 0; i < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++i) {
            if (i != this.recipeBookContainer.getOutputSlot() || !(this.recipeBookContainer instanceof WeaponTableContainer) && !(this.recipeBookContainer instanceof PlayerContainer)) {
                this.giveToPlayer(i);
            }
        }

        this.recipeBookContainer.clear();
    }
}
