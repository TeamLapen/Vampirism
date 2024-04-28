package de.teamlapen.lib.lib.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.*;


public abstract class InventoryContainerMenu extends AbstractContainerMenu {

    protected final ContainerLevelAccess worldPos;
    protected final Container inventory;
    private final int size;

    public InventoryContainerMenu(MenuType<? extends InventoryContainerMenu> containerType, int id, @NotNull Inventory playerInventory, ContainerLevelAccess worldPos, @NotNull Container inventory, SelectorInfo... selectorInfos) {
        this(containerType, id, playerInventory, worldPos, inventory, SelectorSlot::new, selectorInfos);
    }

    public InventoryContainerMenu(MenuType<? extends InventoryContainerMenu> containerType, int id, @NotNull Inventory playerInventory, ContainerLevelAccess worldPos, @NotNull Container inventory, @NotNull SelectorSlotFactory factory, SelectorInfo @NotNull ... selectorInfos) {
        this(containerType, id, worldPos, inventory, selectorInfos.length);

        if (inventory.getContainerSize() < selectorInfos.length) {
            throw new IllegalArgumentException("Inventory size smaller than selector infos");
        }
        inventory.startOpen(playerInventory.player);
        for (int i = 0; i < selectorInfos.length; i++) {
            SelectorSlot slot = factory.create(inventory, i, selectorInfos[i], this::slotsChanged, this::isSlotEnabled);
            slot.setContainer(this);
            this.addSlot(slot);
        }

    }

    private InventoryContainerMenu(MenuType<? extends InventoryContainerMenu> containerType, int id, ContainerLevelAccess worldPos, Container inventory, int size) {
        super(containerType, id);
        this.worldPos = worldPos;
        this.inventory = inventory;
        this.size = size;
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < size) {
                if (!this.moveItemStackTo(slotStack, size, 36 + size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && index < 27 + size) {
                if (!this.moveItemStackTo(slotStack, 0, size, false)) {
                    if (slotStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
                if (!this.moveItemStackTo(slotStack, 27 + size, 36 + size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 + size && index < 36 + size) {
                if (!this.moveItemStackTo(slotStack, 0, 27 + size, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }

        return result;
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        inventory.stopOpen(playerIn);
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    protected void addPlayerSlots(@NotNull Inventory playerInventory, int baseX, int baseY) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, baseX + j * 18, baseY + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, baseX + i * 18, baseY + 58));
        }
    }

    protected void addPlayerSlots(@NotNull Inventory playerInventory) {
        this.addPlayerSlots(playerInventory, 8, 84);
    }

    protected boolean isSlotEnabled(int id) {
        return true;
    }

    @FunctionalInterface
    public interface SelectorSlotFactory {
        SelectorSlot create(Container inventoryIn, int index, SelectorInfo info, Consumer<Container> refreshInvFunc, Function<Integer, Boolean> activeFunc);
    }

    public static class SelectorSlot extends Slot {

        private final @NotNull SelectorInfo info;
        private final Function<Integer, Boolean> activeFunc;
        private final Consumer<Container> refreshInvFunc;
        private InventoryContainerMenu ourContainer;

        public SelectorSlot(@NotNull Container inventoryIn, int index, @NotNull SelectorInfo info, Consumer<Container> refreshInvFunc, Function<Integer, Boolean> activeFunc) {
            super(inventoryIn, index, info.xDisplay, info.yDisplay);
            this.info = info;
            this.activeFunc = activeFunc;
            this.refreshInvFunc = refreshInvFunc;
        }

        public InventoryContainerMenu getContainer() {
            return ourContainer;
        }

        public void setContainer(InventoryContainerMenu container) {
            this.ourContainer = container;
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return info.stackLimit;
        }

        @Override
        public int getMaxStackSize() {
            return info.stackLimit;
        }

        @Nullable
        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return info.background;
        }

        @Override
        public boolean isActive() {
            return activeFunc.apply(this.index);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return info.validate(stack);
        }

        @Override
        public void onQuickCraft(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {
            super.onQuickCraft(oldStackIn, newStackIn);
            this.refreshInvFunc.accept(this.container);
        }

    }

    public static class SelectorInfo {
        public final Predicate<ItemStack> predicate;
        public final int xDisplay;
        public final int yDisplay;
        public final int stackLimit;
        public final boolean inverted;
        /**
         * Pair of atlas and texture id
         */
        @Nullable
        public final Pair<ResourceLocation, ResourceLocation> background;

        public SelectorInfo(Predicate<ItemStack> predicate, int x, int y, boolean inverted, int limit, @Nullable Pair<ResourceLocation, ResourceLocation> background) {
            this.predicate = predicate;
            this.xDisplay = x;
            this.yDisplay = y;
            this.stackLimit = limit;
            this.inverted = inverted;
            this.background = background;
        }

        public SelectorInfo(@NotNull Item item, int x, int y) {
            this(item, x, y, false, 64, null);
        }


        public SelectorInfo(@NotNull Item item, int x, int y, boolean inverted, int stackLimit, @Nullable Pair<ResourceLocation, ResourceLocation> background) {
            this(itemStack -> item.equals(itemStack.getItem()), x, y, inverted, stackLimit, background);
        }

        public SelectorInfo(@NotNull Supplier<Collection<Item>> lazyItemCollection, int x, int y, boolean inverted, int stackLimit, @Nullable Pair<ResourceLocation, ResourceLocation> background) {
            this(itemStack -> lazyItemCollection.get().contains(itemStack.getItem()), x, y, inverted, stackLimit, background);
        }

        public SelectorInfo(@NotNull TagKey<Item> tag, int x, int y) {
            this(tag, x, y, false, 64, null);
        }


        public SelectorInfo(@NotNull TagKey<Item> tag, int x, int y, boolean inverted, int stackLimit, @Nullable Pair<ResourceLocation, ResourceLocation> background) {
            this(itemStack -> itemStack.is(tag), x, y, inverted, stackLimit, background);
        }


        public SelectorInfo(Predicate<ItemStack> predicate, int x, int y) {
            this(predicate, x, y, false);
        }

        public SelectorInfo(Predicate<ItemStack> predicate, int x, int y, boolean inverted) {
            this(predicate, x, y, inverted, 64, null);
        }


        public SelectorInfo(Predicate<ItemStack> predicate, int x, int y, int stackLimit, @Nullable Pair<ResourceLocation, ResourceLocation> background) {
            this(predicate, x, y, false, stackLimit, background);
        }

        public boolean validate(ItemStack s) {
            boolean result = predicate.test(s);
            return result != inverted;
        }


    }
}