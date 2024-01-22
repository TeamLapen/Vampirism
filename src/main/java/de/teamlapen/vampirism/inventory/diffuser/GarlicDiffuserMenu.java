package de.teamlapen.vampirism.inventory.diffuser;

import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.blockentity.diffuser.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModDataMaps;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public class GarlicDiffuserMenu extends DiffuserMenu {

    public GarlicDiffuserMenu(int pContainerId, Inventory playerInventory, PlayerOwnedBlockEntity.LockDataHolder lockData) {
        super(ModContainer.GARLIC_DIFFUSER.get(), pContainerId, playerInventory, lockData);
    }

    public GarlicDiffuserMenu(int pContainerId, Inventory playerInventory, Container pContainer, ContainerData pData, PlayerOwnedBlockEntity.LockDataHolder lockData) {
        super(ModContainer.GARLIC_DIFFUSER.get(), pContainerId, playerInventory, pContainer, pData, lockData);
    }

    @Override
    public boolean isFuel(ItemStack pStack) {
        return pStack.getItemHolder().getData(ModDataMaps.GARLIC_DIFFUSER_FUEL_MAP) != null;
    }

    public static class Factory extends DiffuserMenu.Factory<GarlicDiffuserMenu> {

        @Override
        public GarlicDiffuserMenu create(int windowId, Inventory inv, PlayerOwnedBlockEntity.LockDataHolder lockData) {
            return new GarlicDiffuserMenu(windowId, inv, lockData);
        }
    }
}
