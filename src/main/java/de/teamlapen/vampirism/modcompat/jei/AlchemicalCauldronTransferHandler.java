package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;


public class AlchemicalCauldronTransferHandler implements IRecipeTransferHandler<AlchemicalCauldronContainer> {
    @Override
    public Class<AlchemicalCauldronContainer> getContainerClass() {
        return AlchemicalCauldronContainer.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(AlchemicalCauldronContainer alchemicalCauldronContainer, IRecipeLayout iRecipeLayout, PlayerEntity playerEntity, boolean maxTransfer, boolean doTransfer) {
        return null;
    }
}
