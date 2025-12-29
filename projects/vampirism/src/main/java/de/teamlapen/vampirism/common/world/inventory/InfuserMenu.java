package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.vampirism.common.core.ModMenus;
import de.teamlapen.vampirism.common.util.RecipeHelper;
import de.teamlapen.vampirism.common.world.blockentity.InfuserBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfuserMenu extends AbstractContainerMenu implements InfuserSlots {

    protected final Level level;
    private final ContainerData data;
    private final Container container;
    private final RecipePropertySet acceptedIngredients;
    private final RecipePropertySet acceptedInputs;

    public InfuserMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(9), new SimpleContainerData(2));
    }

    public InfuserMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.INFUSER_MENU.get(), containerId);
        checkContainerSize(container, 9);
        checkContainerDataCount(data, 2);
        this.container = container;
        this.data = data;
        this.level = inventory.player.level();
        this.addInputSlots(container);
        this.addOutputSlots(container);
        this.addStandardInventorySlots(inventory, 8, 84+15);
        this.addDataSlots(data);
        this.acceptedIngredients = RecipeHelper.createLocalInfuserRecipePropertySet(this.level);
        this.acceptedInputs = RecipeHelper.createLocalInfuserRecipeInputPropertySet(this.level);
    }

    private void addInputSlots(Container container) {
        this.addSlot(new Slot(container, 0, 7, 16));
        this.addSlot(new Slot(container, 1, 29, 16));
        this.addSlot(new Slot(container, 2, 51, 16));
        this.addSlot(new Slot(container, 3, 73, 16));
        this.addSlot(new Slot(container, 4, 97, 33+9));
    }

    private void addOutputSlots(Container container) {
        this.addSlot(new ResultSlot(container, 5, 18, 59+9));
        this.addSlot(new ResultSlot(container, 6, 40, 59+9));
        this.addSlot(new ResultSlot(container, 7, 62, 59+9));
        this.addSlot(new ResultSlot(container, 8, 150, 32+9));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index >= SLOT_INGREDIENT_1 && index <= SLOT_RESULT) {
                if (!this.moveItemStackTo(itemstack1, SLOT_RESULT + 1, SLOT_RESULT + 36 +1, false)) {
                    return ItemStack.EMPTY;
                }
            } else{
                if (isIngredient(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, SLOT_INGREDIENT_1, SLOT_INGREDIENT_4 +1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (isInput(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index > SLOT_RESULT && index <= SLOT_RESULT + 27) {
                    if (!this.moveItemStackTo(itemstack1, SLOT_RESULT +1 + 27, SLOT_RESULT + 1 + 36, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index > SLOT_RESULT + 27 && index <= SLOT_RESULT + 27 + 9 && !this.moveItemStackTo(itemstack1, SLOT_RESULT+1, SLOT_RESULT + 27 + 1, false)) {
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(itemstack1, SLOT_RESULT+1, SLOT_RESULT + 36 +1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    private boolean isIngredient(ItemStack stack) {
        return this.acceptedIngredients.test(stack);
    }

    private boolean isInput(ItemStack stack) {
        return this.acceptedInputs.test(stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);

    }

    public float getBurnProgress() {
        int i = this.data.get(InfuserBlockEntity.COOKING_TIMER);
        int j = this.data.get(InfuserBlockEntity.TOTAL_COOKING_TIMER);
        return j != 0 && i != 0 ? Mth.clamp((float)i / (float)j, 0.0F, 1.0F) : 0.0F;
    }

    public static class Factory implements IContainerFactory<InfuserMenu> {

        @Override
        public @NotNull InfuserMenu create(int windowId, @NotNull Inventory inv, @Nullable RegistryFriendlyByteBuf data) {
            return new InfuserMenu(windowId, inv);
        }
    }

    private static class ResultSlot extends Slot {

        public ResultSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }
    }
}
