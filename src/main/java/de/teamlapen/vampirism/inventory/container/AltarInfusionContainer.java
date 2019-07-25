package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;

public class AltarInfusionContainer extends InventoryContainer {
    public static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Ingredient.fromTag(ModTags.Items.PURE_BLOOD), 44, 34), new SelectorInfo(Ingredient.fromItems(ModItems.human_heart), 80, 34), new SelectorInfo(Ingredient.fromItems(ModItems.vampire_book), 116, 34)};
    protected final NonNullList<ItemStack> inventory;

    @Deprecated
    public AltarInfusionContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, NonNullList.withSize(3, ItemStack.EMPTY), IWorldPosCallable.DUMMY);
    }

    public AltarInfusionContainer(int id, PlayerInventory playerInventory, NonNullList<ItemStack> inventory, IWorldPosCallable worldPosCallable) {
        super(ModContainer.altar_infusion, id, playerInventory, worldPosCallable, inventory, SELECTOR_INFOS);
        this.inventory = inventory;
        this.addPlayerSlots(playerInventory);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            clearContainer(playerIn, 3);
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPos, playerIn, ModBlocks.altar_infusion);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index >= 3) {
                if (index < 30) {
                    if (!this.mergeItemStack(slotStack, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    } else if (this.mergeItemStack(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.mergeItemStack(slotStack, 0, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(slotStack, 3, 39, false)) {
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
