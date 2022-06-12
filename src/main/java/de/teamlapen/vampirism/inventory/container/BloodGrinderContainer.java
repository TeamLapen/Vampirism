package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.core.ModContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class BloodGrinderContainer extends InventoryContainer {
    private static final Predicate<ItemStack> canProcess = stack -> BloodConversionRegistry.canBeConverted(stack.getItem());
    public static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(canProcess, 80, 34)};

    @Deprecated
    public BloodGrinderContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(1), ContainerLevelAccess.NULL);
    }

    public BloodGrinderContainer(int id, Inventory playerInventory, Container inventory, ContainerLevelAccess worldPosIn) {
        super(ModContainer.BLOOD_GRINDER.get(), id, playerInventory, worldPosIn, inventory, SELECTOR_INFOS);
        this.addPlayerSlots(playerInventory);
    }

//    @Override
//    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
//        ItemStack result = ItemStack.EMPTY;
//        Slot slot = this.inventorySlots.get(index);
//        if (slot != null && slot.getHasStack()) {
//            ItemStack slotStack = slot.getStack();
//            result = slotStack.copy();
//            if (index >= 1) {
//                if (index < 27) {
//                    if (!this.mergeItemStack(slotStack, 0, 1, false)) {
//                        return ItemStack.EMPTY;
//                    } else if (this.mergeItemStack(slotStack, 27, 36, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                } else {
//                    if (!this.mergeItemStack(slotStack, 0, 27, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
//            } else if (!this.mergeItemStack(slotStack, 1, 36, false)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (slotStack.isEmpty()) {
//                slot.putStack(ItemStack.EMPTY);
//            } else {
//                slot.onSlotChanged();
//            }
//
//            if (slotStack.getCount() == result.getCount()) {
//                return ItemStack.EMPTY;
//            }
//
//            slot.onTake(playerEntity, slotStack);
//        }
//
//        return result;
//    }
}
