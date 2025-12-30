package de.teamlapen.faction.common.world.inventory.base;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
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
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbstractInventoryContainer extends AbstractContainerMenu {

    protected final ContainerLevelAccess access;
    protected final Player player;
    protected final Container inputSlots;
    private final int inputSlotCount;

    protected AbstractInventoryContainer(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, List<SlotDefinition> slotDefinitions) {
        this(menuType,containerId, inventory, access, null, slotDefinitions);
    }

    protected AbstractInventoryContainer(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, @Nullable Container container, List<SlotDefinition> slotDefinitions) {
        super(menuType, containerId);
        this.access = access;
        this.player = inventory.player;
        this.inputSlotCount = slotDefinitions.size();
        this.inputSlots = container != null ? container : this.createInputContainer(this.inputSlotCount);
        this.inputSlots.startOpen(inventory.player);
        this.createInputSlots(slotDefinitions);
    }

    /**
     * Creates the input container. Override to provide a custom container implementation.
     */
    protected Container createInputContainer(int size) {
        return new SimpleContainer(size) {
            @Override
            public void setChanged() {
                super.setChanged();
                AbstractInventoryContainer.this.slotsChanged(this);
            }
        };
    }

    /**
     * Creates input slots based on the slot definitions.
     */
    protected void createInputSlots(List<SlotDefinition> slotDefinitions) {
        for (int i = 0; i < slotDefinitions.size(); i++) {
            SlotDefinition def = slotDefinitions.get(i);
            this.addSlot(createSlot(this.inputSlots, i, def));
        }
    }

    /**
     * Creates a slot for the given definition. Override to provide custom slot implementations.
     */
    protected Slot createSlot(Container container, int index, SlotDefinition definition) {
        return new InputSlot(container, index, definition, this::isSlotEnabled);
    }

    /**
     * Adds the standard player inventory slots (27 main inventory + 9 hotbar).
     */
    protected void addPlayerInventorySlots(Inventory inventory, int x, int y) {
        // Main inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, x + col * 18, y + 58));
        }
    }

    /**
     * Override to control whether a specific input slot is enabled/active.
     */
    protected boolean isSlotEnabled(int slotIndex) {
        return true;
    }

    /**
     * Called when an input slot changes. Override to react to slot changes.
     */
    protected void onInputSlotChanged(Container container) {
        // Default implementation does nothing
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        super.slotsChanged(container);
        if (container == this.inputSlots) {
            this.onInputSlotChanged(container);
        }
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.inputSlots.stopOpen(player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot == null || !slot.hasItem()) {
            return result;
        }

        ItemStack slotStack = slot.getItem();
        result = slotStack.copy();

        int playerInventoryStart = this.inputSlotCount;
        int playerInventoryEnd = playerInventoryStart + 27;
        int hotbarEnd = playerInventoryEnd + 9;

        if (index < this.inputSlotCount) {
            // Moving from input slots to player inventory
            if (!this.moveItemStackTo(slotStack, playerInventoryStart, hotbarEnd, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < playerInventoryEnd) {
            // Moving from main inventory - try input slots first, then hotbar
            if (!this.moveItemStackTo(slotStack, 0, this.inputSlotCount, false)) {
                if (!slotStack.isEmpty() && !this.moveItemStackTo(slotStack, playerInventoryEnd, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (index < hotbarEnd) {
            // Moving from hotbar - try input slots first, then main inventory
            if (!this.moveItemStackTo(slotStack, 0, playerInventoryEnd, false)) {
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

        slot.onTake(player, slotStack);
        return result;
    }

    /**
     * Defines the properties of an input slot.
     */
    public record SlotDefinition(
            Predicate<ItemStack> mayPlace,
            int x,
            int y,
            int maxStackSize,
            @Nullable Identifier backgroundIcon
    ) {
        public SlotDefinition(Predicate<ItemStack> mayPlace, int x, int y) {
            this(mayPlace, x, y, 64, null);
        }

        public SlotDefinition(Item item, int x, int y) {
            this(stack -> stack.is(item), x, y);
        }

        public SlotDefinition(TagKey<Item> tag, int x, int y) {
            this(stack -> stack.is(tag), x, y);
        }

        public SlotDefinition(TagKey<Item> tag, int x, int y, int maxStackSize, @Nullable Identifier backgroundIcon) {
            this(stack -> stack.is(tag), x, y, maxStackSize, backgroundIcon);
        }

        public SlotDefinition(Supplier<Collection<Item>> itemsSupplier, int x, int y, int maxStackSize, @Nullable Identifier backgroundIcon) {
            this(stack -> itemsSupplier.get().contains(stack.getItem()), x, y, maxStackSize, backgroundIcon);
        }

        /**
         * Creates a slot definition with inverted predicate logic.
         */
        public static SlotDefinition inverted(Predicate<ItemStack> mayNotPlace, int x, int y, int maxStackSize, @Nullable Identifier backgroundIcon) {
            return new SlotDefinition(stack -> !mayNotPlace.test(stack), x, y, maxStackSize, backgroundIcon);
        }
    }

    /**
     * A slot that uses SlotDefinition for its behavior.
     */
    public static class InputSlot extends Slot {

        private final SlotDefinition definition;
        private final Predicate<Integer> activeCheck;

        public InputSlot(Container container, int index, SlotDefinition definition, Predicate<Integer> activeCheck) {
            super(container, index, definition.x(), definition.y());
            this.definition = definition;
            this.activeCheck = activeCheck;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return this.definition.mayPlace().test(stack);
        }

        @Override
        public int getMaxStackSize() {
            return this.definition.maxStackSize();
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return Math.min(stack.getMaxStackSize(), this.definition.maxStackSize());
        }

        @Nullable
        @Override
        public Identifier getNoItemIcon() {
            return this.definition.backgroundIcon();
        }

        @Override
        public boolean isActive() {
            return this.activeCheck.test(this.getContainerSlot());
        }
    }
}
