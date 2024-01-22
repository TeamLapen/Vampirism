package de.teamlapen.vampirism.inventory.diffuser;

import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.blockentity.diffuser.FogDiffuserBlockEntity;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModDataMaps;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class FogDiffuserMenu extends DiffuserMenu {
    public FogDiffuserMenu(int pContainerId, Inventory playerInventory, PlayerOwnedBlockEntity.LockDataHolder lockData) {
        super(ModContainer.FOG_DIFFUSER.get(), pContainerId, playerInventory, lockData);
    }

    public FogDiffuserMenu(int pContainerId, Inventory playerInventory, Container pContainer, ContainerData pData, PlayerOwnedBlockEntity.LockDataHolder lockData) {
        super(ModContainer.FOG_DIFFUSER.get(), pContainerId, playerInventory, pContainer, pData, lockData);
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
