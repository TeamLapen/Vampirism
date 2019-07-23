//package de.teamlapen.lib.lib.inventory;
//
//import de.teamlapen.lib.lib.util.ItemStackUtil;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.NonNullList;
//
///**
// * Simple Inventory
// */
//public class SimpleInventory implements InventorySlot.IInventorySlotInventory {
//    protected final NonNullList<InventorySlot> slots;
//    private InventoryContainer container;
//
//    /**
//     * @param slots All slots this inventory has
//     */
//    public SimpleInventory(NonNullList<InventorySlot> slots) {
//        this.slots = slots;
//    }
//
//    @Override
//    public void clear() {
//        for (InventorySlot slot : slots) {
//            slot.stack = ItemStack.EMPTY;
//        }
//    }
//
//    @Override
//    public void closeInventory(PlayerEntity player) {
//
//    }
//
//    @Override
//    public ItemStack decrStackSize(int index, int count) {
//        ItemStack removed = ItemStackUtil.decrIInventoryStackSize(this, index, count);
//        if (!removed.isEmpty()) {
//            onContentChanged();
//        }
//        return removed;
//    }
//
//    @Override
//    public int getInventoryStackLimit() {
//        return 64;
//    }
//
//    @Override
//    public int getSizeInventory() {
//        return slots.size();
//    }
//
//    @Override
//    public NonNullList<InventorySlot> getSlots() {
//        return slots;
//    }
//
//    @Override
//    public ItemStack getStackInSlot(int index) {
//        return index >= getSizeInventory() ? ItemStack.EMPTY : slots.get(index).stack;
//    }
//
//    @Override
//    public InventorySlot getSlot(int index) {
//        return slots.get(index);
//    }
//
//    @Override
//    public boolean isEmpty() {
//        for (InventorySlot slot : this.slots) {
//            if (!slot.stack.isEmpty()) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    @Override
//    public boolean isItemValidForSlot(int slot, ItemStack stack) {
//        if (slots.get(slot).itemSelector != null && !stack.isEmpty()) {
//            return slots.get(slot).itemSelector.isItemAllowed(stack);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean isUsableByPlayer(PlayerEntity player) {
//        return true;
//    }
//
//    @Override
//    public void markDirty() {
//
//    }
//
//    @Override
//    public void openInventory(PlayerEntity player) {
//
//    }
//
//    @Override
//    public ItemStack removeStackFromSlot(int index) {
//        if (this.slots.get(index) != null) {
//            ItemStack itemstack = this.slots.get(index).stack;
//            this.slots.get(index).stack = ItemStack.EMPTY;
//            onContentChanged();
//            return itemstack;
//        } else {
//            return ItemStack.EMPTY;
//        }
//    }
//
//    /**
//     * If the inventory changes {@link InventoryContainer#onInventoryChanged()} is called on the given container
//     *
//     * @param container
//     */
//    public void setChangeListener(InventoryContainer container) {
//        this.container = container;
//    }
//
//    @Override
//    public void setInventorySlotContents(int index, ItemStack stack) {
//        slots.get(index).stack = stack;
//        if (stack.getCount() > getInventoryStackLimit()) {
//            stack.setCount(getInventoryStackLimit());
//        }
//        onContentChanged();
//    }
//
//    private void onContentChanged() {
//        if (container != null) {
//            container.onInventoryChanged();
//        }
//    }
//}