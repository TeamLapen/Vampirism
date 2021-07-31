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
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= selectors.length) return false;
        return selectors[slot].validate(stack);
    }

    @Override
    public void clearContent() {
        inventorySlots.clear();
    }

    @Override
    public int getContainerSize() {
        return inventorySlots.size();
    }

    @Override
    public ItemStack getItem(int index) {
        return inventorySlots.get(index);
    }

    @Override
    public boolean isEmpty() {
        return inventorySlots.isEmpty();
    }

    @Override
    public void load(BlockState state, CompoundNBT tagCompound) {
        super.load(state, tagCompound);
        inventorySlots.clear();
        ItemStackHelper.loadAllItems(tagCompound, this.inventorySlots);

    }

    @Override
    public ItemStack removeItem(int slot, int amt) {
        return ItemStackHelper.removeItem(inventorySlots, slot, amt);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(inventorySlots, index);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        ItemStackHelper.saveAllItems(compound, inventorySlots);
        return compound;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventorySlots.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        this.setChanged();//Not sure

    }

    @Override
    public void startOpen(PlayerEntity player) {
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (!hasLevel()) return false;
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= MAX_DIST_SQRT;
        }
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