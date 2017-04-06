package de.teamlapen.lib.lib.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Used for handling the item exchange between player and block inventory. Should be created with InventoryTileEntity.getNewInventoryContainer()
 *
 * @author Maxanier
 */
public class InventoryContainer extends Container {

    protected InventorySlot.IInventorySlotInventory tile;

    public InventoryContainer(InventoryPlayer invPlayer, InventorySlot.IInventorySlotInventory te) {
        tile = te;
        InventorySlot[] slots = tile.getSlots();
        for (int i = 0; i < slots.length; i++) {
            this.addSlotToContainer(new FilterSlot(tile, i, slots[i].xDisplay, slots[i].yDisplay, slots[i].itemSelector));
        }

        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(invPlayer, i, 8 + i * 18, 142));
        }

        onInventoryChanged();

    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUsableByPlayer(player);
    }

    /**
     * Should be called when the inventory is changed. If overriding this, make sure the inventory actually calls this.
     */
    public void onInventoryChanged() {

    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack stack = null;
        net.minecraft.inventory.Slot slotObject = inventorySlots.get(slot);

        // null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            // merges the item into player inventory since its in the tileEntity
            if (slot < tile.getSlots().length) {
                if (!this.mergeItemStack(stackInSlot, tile.getSlots().length, tile.getSlots().length + 36, true)) {
                    return null;
                }
            }
            // places it into the tileEntity is possible since its in the player inventory
            else if (!this.mergeItemStack(stackInSlot, 0, tile.getSlots().length, false)) {
                return null;
            }

            if (stackInSlot.stackSize == 0) {
                slotObject.putStack(null);
            } else {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.stackSize == stack.stackSize) {
                return null;
            }
            slotObject.onPickupFromSlot(player, stackInSlot);
        }
        return stack;
    }

    public static class FilterSlot extends net.minecraft.inventory.Slot {
        InventorySlot.IItemSelector selector;

        public FilterSlot(IInventory inventory, int index, int xPosition, int yPosition, InventorySlot.IItemSelector selector) {
            super(inventory, index, xPosition, yPosition);
            this.selector = selector;
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack) {
            if (selector != null && stack != null) {
                return selector.isItemAllowed(stack);
            }
            return true;
        }

    }
}