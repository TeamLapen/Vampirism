package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.core.ModMenus;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.mixin.accessor.AbstractContainerMenuAccessor;
import de.teamlapen.vampirism.mixin.accessor.AbstractFurnaceMenuAccessor;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class AlchemicalCauldronMenu extends AbstractFurnaceMenu {

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public AlchemicalCauldronMenu(int id, @NotNull Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(4), new SimpleContainerData(4), ContainerLevelAccess.NULL);
    }

    public AlchemicalCauldronMenu(int id, @NotNull Inventory playerInventory, @NotNull Container inv, @NotNull ContainerData data, ContainerLevelAccess worldPos) {
        super(ModMenus.ALCHEMICAL_CAULDRON.get(), ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get(), RecipeBookType.FURNACE, id, playerInventory, inv, data);
        setSlots(playerInventory);
    }


    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player playerEntity, int index) {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stackCopy = slotStack.copy();
            if (index == 2) {
                if (!this.moveItemStackTo(slotStack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, stackCopy);
            } else if (index != 1 && index != 0 && index != 3) {
                if (this.isFuel(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }

                } else if (!this.moveItemStackTo(slotStack, 0, 2, false)) {
                    return ItemStack.EMPTY;

                } else if (index >= 4 && index < 31) {
                    if (!this.moveItemStackTo(slotStack, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 31 && index < 40 && !this.moveItemStackTo(slotStack, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == stackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }

        return stackCopy;
    }

    private void setSlots(@NotNull Inventory playerInv) {
        this.slots.clear();
        ((AbstractContainerMenuAccessor) this).getLastSlots().clear();
        //Keep order
        this.addSlot(new Slot(((AbstractFurnaceMenuAccessor) this).getContainer(), 0, 44, 17));
        this.addSlot(new Slot(((AbstractFurnaceMenuAccessor)this).getContainer(), 1, 68, 17));
        this.addSlot(new FurnaceResultSlot(playerInv.player, ((AbstractFurnaceMenuAccessor)this).getContainer(), 2, 116, 35));
        this.addSlot(new FurnaceFuelSlot(this, ((AbstractFurnaceMenuAccessor)this).getContainer(), 3, 56, 53));

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
