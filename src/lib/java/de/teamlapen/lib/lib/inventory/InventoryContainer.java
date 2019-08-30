package de.teamlapen.lib.lib.inventory;

import com.mojang.datafixers.util.Either;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;


/**
 * TODO rewrite at some point. Create separate classes for external inventory based and internal item list
 */
public abstract class InventoryContainer extends Container {

    protected final IWorldPosCallable worldPos;
    private final int size;
    /**
     * If non null this should be closed if the container is closed
     */
    @Nullable
    private IInventory inventoryToClose;
    protected IItemHandlerModifiable itemHandler;


    public InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, PlayerInventory playerInventory, IWorldPosCallable worldPos, @Nonnull IInventory inventory, SelectorInfo... selectorInfos) {
        this(containerType, id, worldPos, selectorInfos.length);
        if (inventory.getSizeInventory() < selectorInfos.length) {
            throw new IllegalArgumentException("Inventory size smaller than selector infos");
        }
        inventory.openInventory(playerInventory.player);
        inventoryToClose = inventory;
        itemHandler = new InvWrapper(inventory);
        for (int i = 0; i < selectorInfos.length; i++) {
            this.addSlot(new SelectorSlot(inventory, i, selectorInfos[i]) {
                @Override
                public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
                    super.onSlotChange(p_75220_1_, p_75220_2_);
                    InventoryContainer.this.onCraftMatrixChanged(this.inventory);
                }
            });
        }

    }

    public InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, IWorldPosCallable worldPos, SelectorInfo... selectorInfos) {
        this(containerType, id, worldPos, selectorInfos.length);
        SelectedItemStackHandler handler = new SelectedItemStackHandler(NonNullList.withSize(selectorInfos.length, ItemStack.EMPTY), selectorInfos);
        itemHandler = handler;
        for (int i = 0; i < handler.size; i++) {
            this.addSlot(new SlotItemHandler(handler, i, handler.selectors[i].xDisplay, handler.selectors[i].yDisplay) {
                @Override
                public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
                    super.onSlotChange(p_75220_1_, p_75220_2_);
                    InventoryContainer.this.onCraftMatrixChanged(this.inventory);
                }
            });
        }

    }

    protected void clearContainer(PlayerEntity playerIn) {
        if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerIn).hasDisconnected()) {
            for (int j = 0; j < itemHandler.getSlots(); ++j) {
                playerIn.dropItem(itemHandler.getStackInSlot(j), false);
            }

        } else {
            for (int i = 0; i < itemHandler.getSlots(); ++i) {
                playerIn.inventory.placeItemBackInInventory(playerIn.getEntityWorld(), itemHandler.getStackInSlot(i));
            }

        }
    }

    private InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, IWorldPosCallable worldPos, int size) {
        super(containerType, id);
        this.worldPos = worldPos;
        this.size = size;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (inventoryToClose != null) {
            inventoryToClose.closeInventory(playerIn);
        }
    }

    protected void addPlayerSlots(PlayerInventory playerInventory) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }


    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index >= size && index < 27 + size) {
                if (!this.mergeItemStack(slotStack, 27 + size, 36 + size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 + size && index < 36 + size && !this.mergeItemStack(slotStack, size, 27 + size, false)) {
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

    public static class SelectedItemStackHandler extends ItemStackHandler {
        private final SelectorInfo[] selectors;
        public final int size;

        SelectedItemStackHandler(NonNullList<ItemStack> inventoryIn, SelectorInfo... selectorIn) {
            super(inventoryIn);
            this.selectors = selectorIn;
            this.size = selectorIn.length;
        }

        @Override
        public int getSlotLimit(int slot) {
            return (slot < 0 || slot >= selectors.length) ? 0 : selectors[slot].stackLimit;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= selectors.length) return false;
            return selectors[slot].validate(stack);
        }
    }

    public static class SelectorSlot extends Slot {

        private final SelectorInfo info;

        public SelectorSlot(IInventory inventoryIn, int index, SelectorInfo info) {
            super(inventoryIn, index, info.xDisplay, info.yDisplay);
            this.info = info;
        }

        @Override
        public int getItemStackLimit(ItemStack stack) {
            return info.stackLimit;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return info.validate(stack);
        }
    }


    public static class SelectorInfo {
        public final Either<Ingredient, Function<ItemStack, Boolean>> ingredient;
        public final int xDisplay;
        public final int yDisplay;
        public final int stackLimit;
        public final boolean inverted;

        public SelectorInfo(Either<Ingredient, Function<ItemStack, Boolean>> ingredient, int x, int y, boolean inverted, int limit) {
            this.ingredient = ingredient;
            this.xDisplay = x;
            this.yDisplay = y;
            this.stackLimit = limit;
            this.inverted = inverted;
        }

        public SelectorInfo(Ingredient ingredient, int x, int y) {
            this(ingredient, x, y, false);
        }

        public SelectorInfo(Ingredient ingredient, int x, int y, boolean inverted) {
            this(ingredient, x, y, inverted, 64);
        }

        public SelectorInfo(Ingredient ingredient, int x, int y, boolean inverted, int stackLimit) {
            this(Either.left(ingredient), x, y, inverted, stackLimit);
        }

        public SelectorInfo(Ingredient ingredient, int x, int y, int stackLimit) {
            this(Either.left(ingredient), x, y, false, stackLimit);
        }

        public SelectorInfo(Function<ItemStack, Boolean> ingredient, int x, int y) {
            this(ingredient, x, y, false);
        }

        public SelectorInfo(Function<ItemStack, Boolean> ingredient, int x, int y, boolean inverted) {
            this(ingredient, x, y, inverted, 64);
        }

        public SelectorInfo(Function<ItemStack, Boolean> ingredient, int x, int y, boolean inverted, int stackLimit) {
            this(Either.right(ingredient), x, y, inverted, stackLimit);
        }

        public SelectorInfo(Function<ItemStack, Boolean> ingredient, int x, int y, int stackLimit) {
            this(Either.right(ingredient), x, y, false, stackLimit);
        }

        public boolean validate(ItemStack s) {
            boolean result = ingredient.map(ingredient -> ingredient.test(s) || ingredient.hasNoMatchingItems(), function -> function.apply(s));
            return result != inverted;
        }
    }
}