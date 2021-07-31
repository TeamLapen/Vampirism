package de.teamlapen.lib.lib.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public abstract class InventoryContainer extends Container {

    protected final IWorldPosCallable worldPos;
    protected final IInventory inventory;
    private final int size;

    public InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, PlayerInventory playerInventory, IWorldPosCallable worldPos, @Nonnull IInventory inventory, SelectorInfo... selectorInfos) {
        this(containerType, id, playerInventory, worldPos, inventory, SelectorSlot::new, selectorInfos);
    }

    public InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, PlayerInventory playerInventory, IWorldPosCallable worldPos, @Nonnull IInventory inventory, SelectorSlotFactory factory, SelectorInfo... selectorInfos) {
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

    private InventoryContainer(ContainerType<? extends InventoryContainer> containerType, int id, IWorldPosCallable worldPos, IInventory inventory, int size) {
        super(containerType, id);
        this.worldPos = worldPos;
        this.inventory = inventory;
        this.size = size;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerEntity, int index) {
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
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        inventory.stopOpen(playerIn);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    protected void addPlayerSlots(PlayerInventory playerInventory, int baseX, int baseY) {
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

    protected void addPlayerSlots(PlayerInventory playerInventory) {
        this.addPlayerSlots(playerInventory, 8, 84);
    }

    protected boolean isSlotEnabled(int id) {
        return true;
    }

    @FunctionalInterface
    public interface SelectorSlotFactory {
        SelectorSlot create(IInventory inventoryIn, int index, SelectorInfo info, Consumer<IInventory> refreshInvFunc, Function<Integer, Boolean> activeFunc);
    }

    public static class SelectorSlot extends Slot {

        private final SelectorInfo info;
        private final Function<Integer, Boolean> activeFunc;
        private final Consumer<IInventory> refreshInvFunc;
        private InventoryContainer ourContainer;

        public SelectorSlot(IInventory inventoryIn, int index, SelectorInfo info, Consumer<IInventory> refreshInvFunc, Function<Integer, Boolean> activeFunc) {
            super(inventoryIn, index, info.xDisplay, info.yDisplay);
            this.info = info;
            this.activeFunc = activeFunc;
            this.refreshInvFunc = refreshInvFunc;
        }

        public InventoryContainer getContainer() {
            return ourContainer;
        }

        public void setContainer(InventoryContainer container) {
            this.ourContainer = container;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return info.stackLimit;
        }

        @Override
        public int getMaxStackSize() {
            return info.stackLimit;
        }

        @Nullable
        @OnlyIn(Dist.CLIENT)
        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return info.background;
        }

        @Override
        public boolean isActive() {
            return activeFunc.apply(this.index);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return info.validate(stack);
        }

        @Override
        public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
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

        public SelectorInfo(Item item, int x, int y) {
            this(item, x, y, false, 64, null);
        }


        public SelectorInfo(Item item, int x, int y, boolean inverted, int stackLimit, @Nullable Pair<ResourceLocation, ResourceLocation> background) {
            this(itemStack -> item.equals(itemStack.getItem()), x, y, inverted, stackLimit, background);
        }

        public SelectorInfo(LazyOptional<Collection<Item>> lazyItemCollection, int x, int y, boolean inverted, int stackLimit, @Nullable Pair<ResourceLocation, ResourceLocation> background) {
            this(itemStack -> lazyItemCollection.map(list -> list.contains(itemStack.getItem())).orElse(false), x, y, inverted, stackLimit, background);
        }

        public SelectorInfo(ITag<Item> tag, int x, int y) {
            this(tag, x, y, false, 64, null);
        }


        public SelectorInfo(ITag<Item> tag, int x, int y, boolean inverted, int stackLimit, @Nullable Pair<ResourceLocation, ResourceLocation> background) {
            this(itemStack -> tag.contains(itemStack.getItem()) || tag.getValues().isEmpty(), x, y, inverted, stackLimit, background);
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