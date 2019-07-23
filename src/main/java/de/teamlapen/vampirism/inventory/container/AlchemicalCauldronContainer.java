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
import net.minecraft.util.IntArray;

/**
 * 1.14
 */
public class AlchemicalCauldronContainer extends AbstractFurnaceContainer {

    @Deprecated
    public AlchemicalCauldronContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(4), new IntArray(4));
    }

    public AlchemicalCauldronContainer(int id, PlayerInventory playerInventory, IInventory inv, IIntArray data) {
        super(ModContainer.alchemical_cauldron, ModRecipes.ALCHEMICAL_CAULDRON_TYPE, id, playerInventory, inv, data);
        setSlots(playerInventory);
    }

    private void setSlots(PlayerInventory playerInv) {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        this.addSlot(new Slot(furnaceInventory, 0, 68, 17));
        this.addSlot(new FurnaceFuelSlot(this, furnaceInventory, 1, 56, 53));
        this.addSlot(new FurnaceResultSlot(playerInv.player, furnaceInventory, 2, 116, 35));
        this.addSlot(new Slot(furnaceInventory, 3, 44, 17));

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

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index == 2) {
                if (!this.mergeItemStack(slotStack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(slotStack, result);
            } else if (index != 1 && index != 0 && index != 3) {
                if (this.func_217057_a(slotStack)) {
                    if (!this.mergeItemStack(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(slotStack)) {
                    if (!this.mergeItemStack(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
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

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }

        return result;
    }
}
