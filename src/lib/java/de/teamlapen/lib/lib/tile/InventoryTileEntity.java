package de.teamlapen.lib.lib.tile;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;


/**
 * Basic abstract class for TileEntitys which need a small inventory (with an gui)
 */
public abstract class InventoryTileEntity extends LockableTileEntity implements INamedContainerProvider {

    /**
     * Maximal squared distance from which the player can access the inventory
     */
    protected final int MAX_DIST_SQRT = 40;
    protected NonNullList<ItemStack> inventorySlots;
    protected InventoryContainer.ItemHandler selector;

    /**
     * @param inventorySlotsIn A slot 'description'. The array should contain one Slot instance for each inventory slot which should be created. The slots must each contain the position where they should be
     *              displayed in the GUI, they can also contain a filter for which tileInventory are allowed. Make sure that these Slot instance are unique on server and client.
     */
    public InventoryTileEntity(TileEntityType<?> tileEntityTypeIn, NonNullList<ItemStack> inventorySlotsIn, InventoryContainer.SelectorInfo... selectorInfos) {
        super(tileEntityTypeIn);
        this.inventorySlots = inventorySlotsIn;
        selector = new InventoryContainer.ItemHandler(inventorySlotsIn, selectorInfos);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        return ItemStackHelper.getAndSplit(inventorySlots, slot, amt);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return selector.isItemValid(slot, stack);
    }

    @Override
    public int getSizeInventory() {
        return inventorySlots.size();
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
        return inventorySlots.get(index);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void openInventory(PlayerEntity player) {
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        inventorySlots.clear();
        for (ItemStack stack : inventorySlots) {
            stack = ItemStack.EMPTY;
        }
        ListNBT tagList = tagCompound.getList("Inventory", 10);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundNBT tag = tagList.getCompound(i);
            byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < inventorySlots.size()) {
                inventorySlots.set(slot, ItemStack.read(tag));
            }
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(inventorySlots, index);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventorySlots.set(slot, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        this.markDirty();//Not sure

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);

        ListNBT itemList = new ListNBT();
        for (int i = 0; i < inventorySlots.size(); i++) {
            ItemStack stack = inventorySlots.get(i);
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
        for (ItemStack s : inventorySlots) {
            if (s.isEmpty() || s.getCount() < s.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

}