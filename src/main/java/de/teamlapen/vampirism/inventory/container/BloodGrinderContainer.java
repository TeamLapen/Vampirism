package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.inventory.SimpleInventory;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;

public class BloodGrinderContainer extends InventoryContainer {

    public BloodGrinderContainer(int id, PlayerInventory playerInventory) {
        super(id, playerInventory, ModContainer.blood_grinder, new BloodGrinderInventory(), IWorldPosCallable.DUMMY);
    }

    public BloodGrinderContainer(int id, PlayerInventory playerInventory, InventorySlot.IInventorySlotInventory inventory, IWorldPosCallable worldPosIn) {
        super(id, playerInventory, ModContainer.blood_grinder, inventory, worldPosIn);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPos, playerIn, ModBlocks.blood_grinder);
    }

    public static class BloodGrinderInventory extends SimpleInventory {
        public BloodGrinderInventory() {
            super(NonNullList.from(new InventorySlot(BloodGrinderInventory::canProcess, 80, 34)));
        }

        public static boolean canProcess(ItemStack stack) {
            return BloodConversionRegistry.getImpureBloodValue(stack) > 0;
        }
    }
}
