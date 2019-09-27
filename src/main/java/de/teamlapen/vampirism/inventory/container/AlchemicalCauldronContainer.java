package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModRecipes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.FurnaceFuelSlot;
import net.minecraft.inventory.container.FurnaceResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;


public class AlchemicalCauldronContainer extends AbstractFurnaceContainer {//TODO 1.14 items should be shift-clicked into furnace
    private IWorldPosCallable worldPosCallable;

    @Deprecated
    public AlchemicalCauldronContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(4), new IntArray(4), IWorldPosCallable.DUMMY);
    }

    public AlchemicalCauldronContainer(int id, PlayerInventory playerInventory, IInventory inv, IIntArray data, IWorldPosCallable worldPos) {
        super(ModContainer.alchemical_cauldron, ModRecipes.ALCHEMICAL_CAULDRON_TYPE, id, playerInventory, inv, data);
        worldPosCallable = worldPos;
        setSlots(playerInventory);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stackCopy = slotStack.copy();
            if (index == 2) {
                if (!this.mergeItemStack(slotStack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(slotStack, stackCopy);
            } else if (index != 1 && index != 0 && index != 3) {
                if (this.isFuel(slotStack)) {
                    if (!this.mergeItemStack(slotStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }

                } else if (!this.mergeItemStack(slotStack, 0, 2, false)) {
                    return ItemStack.EMPTY;

                } else if (index >= 4 && index < 31) {
                    if (!this.mergeItemStack(slotStack, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 31 && index < 40 && !this.mergeItemStack(slotStack, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == stackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }

        return stackCopy;
    }

    private void setSlots(PlayerInventory playerInv) {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        //Keep order
        this.addSlot(new Slot(furnaceInventory, 0, 44, 17));
        this.addSlot(new Slot(furnaceInventory, 1, 68, 17));
        this.addSlot(new FurnaceResultSlot(playerInv.player, furnaceInventory, 2, 116, 35));
        this.addSlot(new FurnaceFuelSlot(this, furnaceInventory, 3, 56, 53));

        int i;
        for (i = 0; i < 3; ++i) {
            for (int s = 0; s < 9; ++s) {
                this.addSlot(new Slot(playerInv, s + i * 9 + 9, 8 + s * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }
}
