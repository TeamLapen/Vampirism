package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class VaporStillMenu extends AbstractContainerMenu {

    private static final Identifier EMPTY_SLOT_FUEL = VIdentifier.mc("container/slot/brewing_fuel");
    private static final Identifier EMPTY_SLOT_POTION = VIdentifier.mc("container/slot/potion");

    private static final int SLOT_COUNT_NORMAL = 6;
    private static final int SLOT_COUNT_EXTENDED = 8;

    private final Container inventory;
    private final ContainerData syncedProperties;
    private final boolean extended;

    public VaporStillMenu(int id, Inventory playerInventory, Container inventory, boolean extended, @Nullable ContainerData syncedProperties) {
        super(ModMenus.EXTENDED_VAPOR_STILL.get(), id);

        int expectedSize = extended ? SLOT_COUNT_EXTENDED : SLOT_COUNT_NORMAL;
        checkContainerSize(inventory, expectedSize);

        this.inventory = inventory;
        this.extended = extended;
        this.syncedProperties = syncedProperties != null ? syncedProperties : new SimpleContainerData(3);

        inventory.startOpen(playerInventory.player);
        addInventorySlots(playerInventory.player.level(), extended);
        addStandardInventorySlots(playerInventory, 8, 84);
        addDataSlots(this.syncedProperties);
    }

    private void addInventorySlots(Level level, boolean extended) {
        PotionBrewing brewing = level.potionBrewing();

        addSlot(new FilteredSlot(inventory, 0, 134, 58, Ingredient.of(Items.BLAZE_POWDER), EMPTY_SLOT_FUEL));

        addSlot(new FilteredSlot(inventory, 1, 143, 22, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidExtraIngredient(stack)));

        addSlot(new FilteredSlot(inventory, 2, 125, 22, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidIngredient(brewing, stack)));

        if (extended) {
            addSlot(new FilteredSlot(inventory, 3, 8, 51, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidInput(brewing, stack), EMPTY_SLOT_POTION));
            addSlot(new FilteredSlot(inventory, 4, 26, 51, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidInput(brewing, stack), EMPTY_SLOT_POTION));
            addSlot(new FilteredSlot(inventory, 5, 44, 51, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidInput(brewing, stack), EMPTY_SLOT_POTION));
            addSlot(new FilteredSlot(inventory, 6, 62, 51, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidInput(brewing, stack), EMPTY_SLOT_POTION));
            addSlot(new FilteredSlot(inventory, 7, 80, 51, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidInput(brewing, stack), EMPTY_SLOT_POTION));
        } else {
            addSlot(new FilteredSlot(inventory, 3, 26, 51, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidInput(brewing, stack), EMPTY_SLOT_POTION));
            addSlot(new FilteredSlot(inventory, 4, 44, 51, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidInput(brewing, stack), EMPTY_SLOT_POTION));
            addSlot(new FilteredSlot(inventory, 5, 62, 51, stack -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidInput(brewing, stack), EMPTY_SLOT_POTION));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.inventory.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            int slotCount = extended ? SLOT_COUNT_EXTENDED : SLOT_COUNT_NORMAL;
            int invEnd = slotCount + 36;

            if (index < slotCount) {
                if (!moveItemStackTo(stack, slotCount, invEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(stack, 0, slotCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return result;
    }

    public int getBrewTime() {
        return syncedProperties.get(0);
    }

    public int getFuelTime() {
        return syncedProperties.get(1);
    }

    public int getMaxBrewTime() {
        return syncedProperties.get(2);
    }

    public boolean isExtended() {
        return extended;
    }

    private static class FilteredSlot extends Slot {

        private final Predicate<ItemStack> filter;
        private final @Nullable Identifier noItemIcon;

        public FilteredSlot(Container container, int index, int x, int y, Predicate<ItemStack> filter) {
            this(container, index, x, y, filter, null);
        }

        public FilteredSlot(Container container, int index, int x, int y, Predicate<ItemStack> filter, @Nullable Identifier noItemIcon) {
            super(container, index, x, y);
            this.filter = filter;
            this.noItemIcon = noItemIcon;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return filter.test(stack);
        }

        @Override
        public @Nullable Identifier getNoItemIcon() {
            return noItemIcon;
        }
    }

    public static class Factory implements IContainerFactory<VaporStillMenu> {

        @Override
        public VaporStillMenu create(int windowId, Inventory inv, @Nullable RegistryFriendlyByteBuf data) {
            if (data == null) {
                return new VaporStillMenu(windowId, inv, new SimpleContainer(SLOT_COUNT_NORMAL), false, null);
            }
            boolean extended = data.readBoolean();
            return new VaporStillMenu(windowId, inv, new SimpleContainer(extended ? SLOT_COUNT_EXTENDED : SLOT_COUNT_NORMAL), extended, null);
        }
    }
}
