package de.teamlapen.lib.lib.tile;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;


/**
 * Basic abstract class for TileEntitys which need a small inventory (with an gui)
 */
public abstract class InventoryTileEntity extends TileEntity implements IInventory, InventorySlot.IInventorySlotInventory {

    /**
     * Maximal squared distance from which the player can access the inventory
     */
    protected final int MAX_DIST_SQRT = 40;
    private InventorySlot[] slots;

    /**
     * @param slots A slot 'description'. The array should contain one Slot instance for each inventory slot which should be created. The slots must each contain the position where they should be
     *              displayed in the GUI, they can also contain a filter for which items are allowed. Make sure that these Slot instance are unique on server and client.
     */
    public InventoryTileEntity(InventorySlot[] slots) {
        this.slots = slots;
    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public InventorySlot[] getSlots() {
        return slots;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            if (stack.stackSize <= amt) {
                setInventorySlotContents(slot, null);
            } else {
                stack = stack.splitStack(amt);
                if (stack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
            return stack;
        }
        return null;
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

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public Container getNewInventoryContainer(InventoryPlayer inv) {
        return new InventoryContainer(inv, this);
    }

    @Override
    public int getSizeInventory() {
        return slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slots[slot].stack;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    public IChatComponent getDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slots[slot].itemSelector != null) {
            return slots[slot].itemSelector.isItemAllowed(stack);
        }
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < slots.length; i++) {
            slots[i] = null;
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(getPos()) < MAX_DIST_SQRT;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        for (int i = 0; i < slots.length; i++) {
            slots[i].stack = null;
        }
        NBTTagList tagList = tagCompound.getTagList("Inventory", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < slots.length) {
                slots[slot].stack = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        slots[slot].stack = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = slots[i].stack;
            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                stack.writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }
        tagCompound.setTag("Inventory", itemList);
    }









}