package de.teamlapen.vampirism.entity.minion.management;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.List;


public class MinionInventory implements IInventory {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(25, ItemStack.EMPTY);
    private final NonNullList<ItemStack> inventoryHands = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(this.inventoryHands, this.inventoryArmor, this.inventory);
    private int availableSize;

    public MinionInventory(int availableSize) {
        assert availableSize == 9 || availableSize == 12 || availableSize == 15; //See {@link MinionContainer}
        this.availableSize = availableSize;
    }

    public MinionInventory() {
        this(9);
    }

    public void clear() {
        for (List<ItemStack> list : this.allInventories) {
            list.clear();
        }

    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack s = getStackInSlot(index);
        return !s.isEmpty() && count > 0 ? s.split(count) : ItemStack.EMPTY;
    }

    public int getAvailableSize() {
        return availableSize;
    }

    public void read(ListNBT nbtTagListIn) {
        this.inventory.clear();
        this.inventoryArmor.clear();
        this.inventoryHands.clear();

        for (int i = 0; i < nbtTagListIn.size(); ++i) {
            CompoundNBT compoundnbt = nbtTagListIn.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.read(compoundnbt);
            if (!itemstack.isEmpty()) {
                if (j < this.inventoryHands.size()) {
                    this.inventoryHands.set(j, itemstack);
                } else if (j >= 10 && j < this.inventoryArmor.size() + 10) {
                    this.inventoryArmor.set(j - 10, itemstack);
                } else if (j >= 20 && j < this.inventory.size() + 20) {
                    this.inventory.set(j - 20, itemstack);
                }
            }
        }

    }

    public NonNullList<ItemStack> getInventoryArmor() {
        return inventoryArmor;
    }

    public NonNullList<ItemStack> getInventoryHands() {
        return inventoryHands;
    }

    @Override
    public int getSizeInventory() {
        return 6 + availableSize;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        assert index >= 0;
        if (index < 2) {
            return inventoryHands.get(index);
        } else if (index < 6) {
            return inventoryArmor.get(index - 2);
        } else if (index < 6 + availableSize) {
            return inventory.get(index - 6);
        }
        return ItemStack.EMPTY;
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemstack1 : this.inventoryHands) {
            if (!itemstack1.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemstack2 : this.inventoryArmor) {
            if (!itemstack2.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void markDirty() {

    }

    public MinionInventory setAvailableSize(int newSize) {
        assert newSize == 9 || availableSize == 12 || availableSize == 15;
        this.availableSize = newSize;
        return this;
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack s = getStackInSlot(index);
        if (!s.isEmpty()) {
            this.setInventorySlotContents(index, ItemStack.EMPTY);
        }
        return s;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        assert index >= 0;
        if (index < 2) {
            inventoryHands.set(index, stack);
        } else if (index < 6) {
            inventoryArmor.set(index - 2, stack);
        } else if (index < 6 + availableSize) {
            inventory.set(index - 6, stack);
        }
    }

    public ListNBT write(ListNBT nbt) {
        for (int i = 0; i < this.inventoryHands.size(); ++i) {
            if (!this.inventoryHands.get(i).isEmpty()) {
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putByte("Slot", (byte) i);
                this.inventoryHands.get(i).write(compoundnbt);
                nbt.add(compoundnbt);
            }
        }

        for (int j = 0; j < this.inventoryArmor.size(); ++j) {
            if (!this.inventoryArmor.get(j).isEmpty()) {
                CompoundNBT compoundnbt1 = new CompoundNBT();
                compoundnbt1.putByte("Slot", (byte) (j + 10));
                this.inventoryArmor.get(j).write(compoundnbt1);
                nbt.add(compoundnbt1);
            }
        }

        for (int k = 0; k < this.inventory.size(); ++k) {
            if (!this.inventory.get(k).isEmpty()) {
                CompoundNBT compoundnbt2 = new CompoundNBT();
                compoundnbt2.putByte("Slot", (byte) (k + 20));
                this.inventory.get(k).write(compoundnbt2);
                nbt.add(compoundnbt2);
            }
        }
        return nbt;
    }
}
