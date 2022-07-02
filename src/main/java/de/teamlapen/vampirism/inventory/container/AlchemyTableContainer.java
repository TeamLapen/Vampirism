package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class AlchemyTableContainer extends AbstractContainerMenu {

    private final Container alchemyTable;
    private final ContainerData alchemyTableData;
    private final Slot ingredientSlot;

    public AlchemyTableContainer(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory.player.level, playerInventory, new SimpleContainer(6), new SimpleContainerData(3));
    }

    public AlchemyTableContainer(int containerId, Level level, Inventory playerInventory, Container inventory, ContainerData data) {
        super(ModContainer.ALCHEMICAL_TABLE.get(), containerId);
        checkContainerSize(inventory, 5);
        checkContainerDataCount(data, 3);
        this.alchemyTable = inventory;
        this.alchemyTableData = data;
        this.addSlot(new OilSlot(level, inventory, 0, 55, 16));
        this.addSlot(new OilSlot(level, inventory, 1, 79, 16));
        this.addSlot(new ResultSlot(inventory, 2, 112,72));
        this.addSlot(new ResultSlot(inventory, 3, 140,44));
        this.ingredientSlot = this.addSlot(new IngredientSlot(level, inventory, 4, 15, 25));
        this.addSlot(new FuelSlot(inventory, 5, 34, 69));
        this.addDataSlots(data);

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + 17 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142 + 17));
        }

    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return this.alchemyTable.stillValid(player);
    }

    public int getFuel() {
        return this.alchemyTableData.get(1);
    }

    public int getBrewingTicks() {
        return this.alchemyTableData.get(0);
    }

    public int getColor() {
        return this.alchemyTableData.get(2);
    }


    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int slotId) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotId < 0 || slotId > 5) {
                if (FuelSlot.mayPlaceItem(itemstack)) {
                    if (this.moveItemStackTo(itemstack1, 5, 6, false) || this.ingredientSlot.mayPlace(itemstack1) && !this.moveItemStackTo(itemstack1, 4, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.ingredientSlot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 4, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (OilSlot.mayPlaceItem(player.level, itemstack) && itemstack.getCount() == 1) {
                    if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotId >= 6 && slotId < 33) {
                    if (!this.moveItemStackTo(itemstack1, 33, 42, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotId >= 33 && slotId < 42) {
                    if (!this.moveItemStackTo(itemstack1, 6, 33, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 6, 42, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
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


    static class OilSlot extends Slot {

        private final Level world;

        public OilSlot(Level worldPos, Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
            this.world = worldPos;
        }

        public static boolean mayPlaceItem(Level world, ItemStack itemstack) {
            return world.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).stream().anyMatch(recipe -> recipe.isIngredient(itemstack));
        }

        public boolean mayPlace(@Nonnull ItemStack stack) {
            return mayPlaceItem(world, stack);
        }

        public int getMaxStackSize() {
            return 1;
        }
    }

    static class IngredientSlot extends Slot {
        private final Level world;

        public IngredientSlot(Level worldPos, Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
            this.world = worldPos;

        }

        public boolean mayPlace(@Nonnull ItemStack stack) {
            return this.world.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).stream().anyMatch(recipe -> recipe.isInput(stack));
        }

        public int getMaxStackSize() {
            return 64;
        }
    }

    static class FuelSlot extends Slot {
        public FuelSlot(Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
        }

        public boolean mayPlace(@Nonnull ItemStack stack) {
            return mayPlaceItem(stack);
        }

        public static boolean mayPlaceItem(ItemStack stack) {
            return stack.getItem() == Items.BLAZE_POWDER;
        }

        public int getMaxStackSize() {
            return 64;
        }
    }

    static class ResultSlot extends Slot {

        public ResultSlot(Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return false;
        }
    }
}
