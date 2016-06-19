package de.teamlapen.lib.lib.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Simple Inventory
 */
public abstract class SimpleInventory implements InventorySlot.IInventorySlotInventory {
    protected final InventorySlot[] slots;
    private InventoryContainer container;

    /**
     * @param slots All slots this inventory has
     */
    public SimpleInventory(InventorySlot[] slots) {
        this.slots = slots;

    }

    @Override
    public void clear() {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getStackInSlot(index);
        if (stack != null) {
            if (stack.stackSize <= count) {
                setInventorySlotContents(index, null);
            } else {
                stack = stack.splitStack(count);
                if (stack.stackSize == 0) {
                    setInventorySlotContents(index, null);
                }
            }
            return stack;
        }
        return null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public int getSizeInventory() {
        return slots.length;
    }

    @Override
    public InventorySlot[] getSlots() {
        return slots;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= getSizeInventory() ? null : slots[index].stack;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slots[slot].itemSelector != null && stack != null) {
            return slots[slot].itemSelector.isItemAllowed(stack);
        }
        return true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.slots[index] != null) {
            ItemStack itemstack = this.slots[index].stack;
            this.slots[index].stack = null;
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * If the inventory changes {@link InventoryContainer#onInventoryChanged()} is called on the given container
     *
     * @param container
     */
    public void setChangeListener(InventoryContainer container) {
        this.container = container;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        slots[index].stack = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        onContentChanged();
    }

    private void onContentChanged() {
        if (container != null) {
            container.onInventoryChanged();
        }
    }
}