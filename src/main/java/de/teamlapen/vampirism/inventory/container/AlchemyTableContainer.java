package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModRecipes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class AlchemyTableContainer extends Container {

    private final IInventory alchemyTable;
    private final IIntArray alchemyTableData;
    private final Slot ingredientSlot;

    public AlchemyTableContainer(int containerId, PlayerInventory playerInventory) {
        this(containerId, playerInventory.player.level, playerInventory, new Inventory(6), new IntArray(3));
    }

    public AlchemyTableContainer(int containerId, World level, PlayerInventory playerInventory, IInventory inventory, IIntArray data) {
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
    public boolean stillValid(@Nonnull PlayerEntity player) {
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
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotId) {
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

        private final World world;

        public OilSlot(World worldPos, IInventory inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
            this.world = worldPos;
        }

        public static boolean mayPlaceItem(World world, ItemStack itemstack) {
            return world.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().anyMatch(recipe -> recipe.isIngredient(itemstack));
        }

        public boolean mayPlace(@Nonnull ItemStack stack) {
            return mayPlaceItem(world, stack);
        }

        public int getMaxStackSize() {
            return 1;
        }

        @Nonnull
        public ItemStack onTake(@Nonnull PlayerEntity player, @Nonnull ItemStack stack) {
//            IOil potion = OilUtils.getOil(stack);
//            if (player instanceof ServerPlayerEntity) {
//                CriteriaTriggers.BREWED_POTION.trigger((ServerPlayerEntity)player, potion);
//            }

            super.onTake(player, stack);
            return stack;
        }
    }

    static class IngredientSlot extends Slot {
        private final World world;

        public IngredientSlot(World worldPos, IInventory inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
            this.world = worldPos;

        }

        public boolean mayPlace(@Nonnull ItemStack stack) {
            return this.world.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().anyMatch(recipe -> recipe.isInput(stack));
        }

        public int getMaxStackSize() {
            return 64;
        }
    }

    static class FuelSlot extends Slot {
        public FuelSlot(IInventory inventory, int slotId, int xPos, int yPos) {
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

        public ResultSlot(IInventory inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return false;
        }
    }
}
