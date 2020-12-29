package de.teamlapen.lib.lib.tile;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;


/**
 * Basic abstract class for TileEntitys which need a small inventory (with an gui)
 */
public abstract class InventoryTileEntity extends LockableTileEntity implements INamedContainerProvider {

    /**
     * Maximal squared distance from which the player can access the inventory
     */
    protected final int MAX_DIST_SQRT = 64;
    protected NonNullList<ItemStack> inventorySlots;
    protected InventoryContainer.SelectorInfo[] selectors;


    public InventoryTileEntity(TileEntityType<?> tileEntityTypeIn, int size, InventoryContainer.SelectorInfo... selectorInfos) {
        super(tileEntityTypeIn);
        this.inventorySlots = NonNullList.withSize(size, ItemStack.EMPTY);
        if (selectorInfos.length != size) {
            throw new IllegalArgumentException("Selector count must match inventory size");
        }
        selectors = selectorInfos;
    }

    @Override
    public void clear() {
        inventorySlots.clear();
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        return ItemStackHelper.getAndSplit(inventorySlots, slot, amt);
    }

    @Override
    public int getSizeInventory() {
        return inventorySlots.size();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventorySlots.get(index);
    }

    @Override
    public boolean isEmpty() {
        return inventorySlots.isEmpty();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot < 0 || slot >= selectors.length) return false;
        return selectors[slot].validate(stack);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (!hasWorld()) return false;
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= MAX_DIST_SQRT;
        }
    }

    @Override
    public void openInventory(PlayerEntity player) {
    }

    @Override
    public void read(BlockState state, CompoundNBT tagCompound) {
        super.read(state, tagCompound);
        inventorySlots.clear();
        ItemStackHelper.loadAllItems(tagCompound, this.inventorySlots);

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
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, inventorySlots);
        return compound;
    }

    @Nonnull
    protected InvWrapper createWrapper() {
        return new SelectorInvWrapper(this);
    }

    protected boolean isFull() {
        for (ItemStack s : inventorySlots) {
            if (s.isEmpty() || s.getCount() < s.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    private class SelectorInvWrapper extends InvWrapper {

        SelectorInvWrapper(IInventory inv) {
            super(inv);
        }

        @Override
        public int getSlotLimit(int slot) {
            return (slot < 0 || slot >= selectors.length) ? 0 : selectors[slot].stackLimit;
        }
    }

}