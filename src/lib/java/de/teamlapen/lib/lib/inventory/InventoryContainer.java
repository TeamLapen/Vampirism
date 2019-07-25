package de.teamlapen.lib.lib.inventory;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

/**
 * Used for handling the item exchange between player and block inventory. Should be created with InventoryTileEntity.getNewInventoryContainer()
 *
 * @author Maxanier
 */
public abstract class InventoryContainer extends Container {

    protected IWorldPosCallable worldPos;
    protected ItemHandler selector;

    /**
     * base constructor with selector for filtering slots
     *
     * @param containerType
     * @param id
     * @param playerInventory
     * @param selectorInfos
     */
    public InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, PlayerInventory playerInventory, SelectorInfo... selectorInfos) {
        super(containerType, id);
        this.selector = new ItemHandler(inventoryItemStacks, selectorInfos);
        for (int i = 0; i < selectorInfos.length; i++) {
            this.addSlot(new SlotItemHandler(selector, i, selectorInfos[i].xDisplay, selectorInfos[i].yDisplay) {
                @Override
                public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
                    super.onSlotChange(p_75220_1_, p_75220_2_);
                    InventoryContainer.this.onCraftMatrixChanged(this.inventory);
                }
            });
        }
        this.worldPos = IWorldPosCallable.DUMMY;
    }

    public InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, PlayerInventory playerInventory, IWorldPosCallable worldPos, SelectorInfo... selectorInfos) {
        this(containerType, id, playerInventory, selectorInfos);
        this.worldPos = worldPos;
    }

    /**
     * base constructor with selector for filtering slots for tileentities
     *
     * @param containerType
     * @param id
     * @param playerInventory
     * @param inventory
     * @param selectorInfos
     */
    public InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, PlayerInventory playerInventory, NonNullList<ItemStack> inventory, SelectorInfo... selectorInfos) {
        super(containerType, id);
        this.selector = new ItemHandler(inventory, selectorInfos);
        for (int i = 0; i < selectorInfos.length; i++) {
            this.addSlot(new SlotItemHandler(selector, i, selectorInfos[i].xDisplay, selectorInfos[i].yDisplay) {
                @Override
                public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
                    super.onSlotChange(p_75220_1_, p_75220_2_);
                    InventoryContainer.this.onCraftMatrixChanged(this.inventory);
                }
            });
        }
        this.worldPos = IWorldPosCallable.DUMMY;
    }

    public InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, PlayerInventory playerInventory, IWorldPosCallable worldPos, NonNullList<ItemStack> inventory, SelectorInfo... selectorInfos) {
        this(containerType, id, playerInventory, inventory, selectorInfos);
        this.worldPos = worldPos;
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

    protected void clearContainer(PlayerEntity playerEntity, int lastIndex) {
        if (!playerEntity.isAlive() || playerEntity instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerEntity).hasDisconnected()) {
            for (int j = 0; j < lastIndex + 1; ++j) {
                playerEntity.dropItem(ItemStackHelper.getAndRemove(inventoryItemStacks, j), false);
            }

        } else {
            for (int i = 0; i < lastIndex + 1; ++i) {
                playerEntity.inventory.placeItemBackInInventory(playerEntity.getEntityWorld(), ItemStackHelper.getAndRemove(inventoryItemStacks, i));
            }

        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            int size = selector.selector.size();
            if (index >= 0 + size && index < 27 + size) {
                if (!this.mergeItemStack(slotStack, 27 + size, 36 + size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 + size && index < 36 + size && !this.mergeItemStack(slotStack, 0 + size, 27 + size, false)) {
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

    public static class ItemHandler extends ItemStackHandler {
        private final NonNullList<Either<Ingredient, Function<ItemStack, Boolean>>> selector = NonNullList.create();
        private final List<Integer> limits = Lists.newArrayList();
        private final List<Boolean> inverted = Lists.newArrayList();

        public ItemHandler(NonNullList<ItemStack> inventoryIn, SelectorInfo... selectorIn) {
            super(inventoryIn);
            for (SelectorInfo info : selectorIn) {
                selector.add(info.ingredient);
                limits.add(info.stackLimit);
                inverted.add(info.inverted);
            }
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            boolean result = false;
            if (selector.get(slot).left().isPresent()) {
                result = selector.get(slot).left().get().test(stack) || selector.get(slot).left().get().hasNoMatchingItems();
            } else if (selector.get(slot).right().isPresent()) {
                result = selector.get(slot).right().get().apply(stack);
            } else {
                return false;
            }
            return inverted.get(slot) ? !result : result;
        }

        @Override
        public int getSlotLimit(int slot) {
            return limits.get(slot);
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
    }
}