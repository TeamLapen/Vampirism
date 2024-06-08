package de.teamlapen.vampirism.inventory.diffuser;

import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.core.ModDataMaps;
import de.teamlapen.vampirism.core.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public class FogDiffuserMenu extends DiffuserMenu {
    public FogDiffuserMenu(int pContainerId, Inventory playerInventory, PlayerOwnedBlockEntity.LockDataHolder lockData) {
        super(ModMenus.FOG_DIFFUSER.get(), pContainerId, playerInventory, lockData);
    }

    public FogDiffuserMenu(int pContainerId, Inventory playerInventory, Container pContainer, ContainerData pData, PlayerOwnedBlockEntity.LockDataHolder lockData) {
        super(ModMenus.FOG_DIFFUSER.get(), pContainerId, playerInventory, pContainer, pData, lockData);
    }

    @Override
    public boolean isFuel(ItemStack pStack) {
        return pStack.getItemHolder().getData(ModDataMaps.FOG_DIFFUSER_FUEL_MAP) != null;
    }

    public static class Factory extends DiffuserMenu.Factory<FogDiffuserMenu> {

        @Override
        public FogDiffuserMenu create(int windowId, Inventory inv, PlayerOwnedBlockEntity.LockDataHolder lockData) {
            return new FogDiffuserMenu(windowId, inv, lockData);
        }

    }
}
