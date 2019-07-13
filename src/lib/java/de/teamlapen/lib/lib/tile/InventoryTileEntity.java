package de.teamlapen.lib.lib.tile;

import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;


/**
 * Basic abstract class for TileEntitys which need a small inventory (with an gui)
 */
public abstract class InventoryTileEntity extends LockableTileEntity implements INamedContainerProvider {

    /**
     * Maximal squared distance from which the player can access the inventory
     */
    protected final int MAX_DIST_SQRT = 40;
    protected InventorySlot.IInventorySlotInventory inventorySlots;

    /**
     * @param inventorySlotsIn A slot 'description'. The array should contain one Slot instance for each inventory slot which should be created. The slots must each contain the position where they should be
     *              displayed in the GUI, they can also contain a filter for which items are allowed. Make sure that these Slot instance are unique on server and client.
     */
    public InventoryTileEntity(TileEntityType<?> tileEntityTypeIn, InventorySlot.IInventorySlotInventory inventorySlotsIn) {
        super(tileEntityTypeIn);
        this.inventorySlots = inventorySlotsIn;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        return ItemStackUtil.decrIInventoryStackSize(this, slot, amt);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return inventorySlots.isItemValidForSlot(slot, stack);
    }

    @Override
    public int getSizeInventory() {
        return inventorySlots.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return inventorySlots.isEmpty();
    }

    @Override
    public void clear() {
        inventorySlots.clear();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventorySlots.getStackInSlot(index);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return inventorySlots.isUsableByPlayer(player);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        inventorySlots.openInventory(player);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        inventorySlots.clear();
        for (InventorySlot slot1 : inventorySlots.getSlots()) {
            slot1.stack = ItemStack.EMPTY;
        }
        ListNBT tagList = tagCompound.getList("Inventory", 10);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundNBT tag = tagList.getCompound(i);
            byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < inventorySlots.getSizeInventory()) {
                inventorySlots.getSlot(slot).stack = ItemStack.read(tag);
            }
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return inventorySlots.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventorySlots.getSlot(slot).stack = stack;
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        this.markDirty();//Not sure

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);

        ListNBT itemList = new ListNBT();
        for (int i = 0; i < inventorySlots.getSizeInventory(); i++) {
            ItemStack stack = inventorySlots.getSlot(i).stack;
            if (!stack.isEmpty()) {
                CompoundNBT tag = new CompoundNBT();
                tag.putByte("Slot", (byte) i);
                stack.write(tag);
                itemList.add(tag);
            }
        }
        nbt.put("Inventory", itemList);

        return nbt;
    }


    protected boolean isFull() {
        for (InventorySlot s : inventorySlots.getSlots()) {
            if (this.inventorySlots.getStackInSlot(0).isEmpty() || this.inventorySlots.getStackInSlot(0).getCount() < this.inventorySlots.getStackInSlot(0).getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

}