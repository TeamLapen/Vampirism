package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.core.ModMenus;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipeInput;
import de.teamlapen.vampirism.recipes.ITestableRecipeInput;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class AlchemicalCauldronMenu extends RecipeBookMenu<AlchemicalCauldronRecipeInput, AlchemicalCauldronRecipe> {
    public static final int INGREDIENT_SLOT = 1;
    public static final int FLUID_SLOT = 0;
    public static final int FUEL_SLOT = 3;
    public static final int RESULT_SLOT = 2;
    public static final int SLOT_COUNT = 4;
    public static final int DATA_COUNT = 4;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;
    private final Container container;
    protected final ContainerData data;
    protected final Level level;
    private final RecipeType<? extends AlchemicalCauldronRecipe> recipeType;
    private final RecipeBookType recipeBookType;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public AlchemicalCauldronMenu(int id, @NotNull Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(4), new SimpleContainerData(4));
    }

    public AlchemicalCauldronMenu(int id, @NotNull Inventory playerInventory, @NotNull Container inv, @NotNull ContainerData pData) {
        super(ModMenus.ALCHEMICAL_CAULDRON.get(), id);
        this.recipeType = ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get();
        this.recipeBookType = RecipeBookType.FURNACE;
        checkContainerSize(inv, 3);
        checkContainerDataCount(pData, 4);
        this.container = inv;
        this.data = pData;
        this.level = playerInventory.player.level();
        this.addSlot(new Slot(this.container, FLUID_SLOT, 44, 17));
        this.addSlot(new Slot(this.container, INGREDIENT_SLOT, 68, 17));
        this.addSlot(new FurnaceResultSlot(playerInventory.player, this.container, RESULT_SLOT, 116, 35));
        this.addSlot(new FurnaceFuelSlot(this, this.container, FUEL_SLOT, 56, 53));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(pData);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents pItemHelper) {
        if (this.container instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible)this.container).fillStackedContents(pItemHelper);
        }
    }

    public Optional<RecipeHolder<AlchemicalCauldronRecipe>> checkRecipeNoSkills() {
        return this.level.getRecipeManager().getRecipeFor((RecipeType<AlchemicalCauldronRecipe>) this.recipeType, new AlchemicalCauldronRecipeInput(this.container.getItem(INGREDIENT_SLOT), this.container.getItem(FLUID_SLOT), ITestableRecipeInput.TestType.BOTH), this.level);
    }

    @Override
    public void clearCraftingContent() {
        this.getSlot(FLUID_SLOT).set(ItemStack.EMPTY);
        this.getSlot(INGREDIENT_SLOT).set(ItemStack.EMPTY);
        this.getSlot(RESULT_SLOT).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(RecipeHolder<AlchemicalCauldronRecipe> pRecipe) {
        return pRecipe.value().matches(new AlchemicalCauldronRecipeInput(this.container.getItem(INGREDIENT_SLOT), this.container.getItem(FLUID_SLOT)), this.level);
    }

    @Override
    public int getResultSlotIndex() {
        return RESULT_SLOT;
    }

    @Override
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == FUEL_SLOT) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex != INGREDIENT_SLOT && pIndex != RESULT_SLOT && pIndex != FLUID_SLOT) {
                var asFluid = this.canSmeltAsFluid(itemstack1) && !this.moveItemStackTo(itemstack1, FLUID_SLOT, FLUID_SLOT + 1, false);
                var asIngredient = this.canSmeltAsIngredient(itemstack1) && !this.moveItemStackTo(itemstack1, INGREDIENT_SLOT, INGREDIENT_SLOT + 1, false);
                if (asFluid || asIngredient) {
                        return ItemStack.EMPTY;
                } else if (this.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, FUEL_SLOT, FUEL_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= INV_SLOT_START && pIndex < INV_SLOT_END) {
                    if (!this.moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= INV_SLOT_END && pIndex < USE_ROW_SLOT_END && !this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    protected boolean canSmeltAsIngredient(ItemStack pStack) {
        return this.level.getRecipeManager().getRecipeFor((RecipeType<AlchemicalCauldronRecipe>) this.recipeType, new AlchemicalCauldronRecipeInput(pStack, this.container.getItem(FLUID_SLOT), ITestableRecipeInput.TestType.INPUT_1), this.level).isPresent();
    }

    protected boolean canSmeltAsFluid(ItemStack pStack) {
        return this.level.getRecipeManager().getRecipeFor((RecipeType<AlchemicalCauldronRecipe>) this.recipeType, new AlchemicalCauldronRecipeInput(this.container.getItem(INGREDIENT_SLOT), pStack, ITestableRecipeInput.TestType.INPUT_2), this.level).isPresent();
    }

    protected boolean isFuel(ItemStack pStack) {
        return pStack.getBurnTime(this.recipeType) > 0;
    }

    public float getBurnProgress() {
        int i = this.data.get(2);
        int j = this.data.get(3);
        return j != 0 && i != 0 ? Mth.clamp((float)i / (float)j, 0.0F, 1.0F) : 0.0F;
    }

    public float getLitProgress() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 200;
        }

        return Mth.clamp((float)this.data.get(0) / (float)i, 0.0F, 1.0F);
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return this.recipeBookType;
    }

    @Override
    public boolean shouldMoveToInventory(int pSlotIndex) {
        return pSlotIndex != 2;
    }

    public static class FurnaceFuelSlot extends Slot {
        private final AlchemicalCauldronMenu menu;

        public FurnaceFuelSlot(AlchemicalCauldronMenu pFurnaceMenu, Container pFurnaceContainer, int pSlot, int pXPosition, int pYPosition) {
            super(pFurnaceContainer, pSlot, pXPosition, pYPosition);
            this.menu = pFurnaceMenu;
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        @Override
        public boolean mayPlace(ItemStack pStack) {
            return this.menu.isFuel(pStack) || isBucket(pStack);
        }

        @Override
        public int getMaxStackSize(ItemStack pStack) {
            return isBucket(pStack) ? 1 : super.getMaxStackSize(pStack);
        }

        public static boolean isBucket(ItemStack pStack) {
            return pStack.is(Items.BUCKET);
        }
    }
}
