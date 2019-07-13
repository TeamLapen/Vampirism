package de.teamlapen.lib.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Used for handling the item exchange between player and block inventory. Should be created with InventoryTileEntity.getNewInventoryContainer()
 *
 * @author Maxanier
 */
public abstract class InventoryContainer extends Container {

    protected InventorySlot.IInventorySlotInventory inventory;
    protected IWorldPosCallable worldPos;

    public InventoryContainer(int id, PlayerInventory playerInventory, ContainerType containerType, InventorySlot.IInventorySlotInventory inventoryIn) {
        this(id, playerInventory, containerType, inventoryIn, IWorldPosCallable.DUMMY);
    }

    public InventoryContainer(int id, PlayerInventory playerInventory, ContainerType containerType, InventorySlot.IInventorySlotInventory inventoryIn, IWorldPosCallable worldPosIn) {
        super(containerType, id);
        this.inventory = inventoryIn;
        this.worldPos = worldPosIn;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            this.addSlot(new FilterSlot(inventory, i, inventory.getSlot(i).xDisplay, inventory.getSlot(i).yDisplay, inventory.getSlot(i).itemSelector));
        }

        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        onInventoryChanged();

    }

    /**
     * Should be called when the inventory is changed. If overriding this, make sure the inventory actually calls this.
     */
    public void onInventoryChanged() {

    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);

        // null checks and checks if the item can be stacked (maxStackSize > 1)
        ItemStack stackInSlot;
        if (slotObject != null && !(stackInSlot = slotObject.getStack()).isEmpty()) {
            stack = stackInSlot.copy();
            // merges the item into player inventory since its in the tileEntity
            if (slot < inventory.getSizeInventory()) {
                if (!this.mergeItemStack(stackInSlot, inventory.getSizeInventory(), inventory.getSizeInventory() + 36, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // places it into the tileEntity is possible since its in the player inventory
            else if (!this.mergeItemStack(stackInSlot, 0, inventory.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slotObject.putStack(ItemStack.EMPTY);
            } else {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slotObject.onTake(player, stackInSlot);
        }
        return stack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean result = super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
        onInventoryChanged();//Always update inventory status. Dirty workaround. Probably triggering the update way to often.  @TODO-issue Rewrite inventory code
        return result;
    }

    public static class FilterSlot extends Slot {
        InventorySlot.IItemSelector selector;

        public FilterSlot(IInventory inventory, int index, int xPosition, int yPosition, InventorySlot.IItemSelector selector) {
            super(inventory, index, xPosition, yPosition);
            this.selector = selector;
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack) {
            return !(selector != null && stack != null) || selector.isItemAllowed(stack);
        }

    }
}