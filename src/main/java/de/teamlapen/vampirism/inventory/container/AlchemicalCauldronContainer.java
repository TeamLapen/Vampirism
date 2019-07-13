package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModRecipes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.util.IIntArray;

/**
 * 1.14
 */
public class AlchemicalCauldronContainer extends AbstractFurnaceContainer {

    public AlchemicalCauldronContainer(int id, PlayerInventory playerInventory) {
        super(ModContainer.alchemical_cauldron, ModRecipes.ALCHEMICAL_CAULDRON_TYPE, id, playerInventory);
    }

    public AlchemicalCauldronContainer(int id, PlayerInventory invPlayer, IInventory inv, IIntArray data) {
        super(ModContainer.alchemical_cauldron, ModRecipes.ALCHEMICAL_CAULDRON_TYPE, id, invPlayer, inv, data);
    }
}
