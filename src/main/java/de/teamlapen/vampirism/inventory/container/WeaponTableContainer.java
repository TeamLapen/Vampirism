package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.inventory.BooleanDataSlot;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.inventory.inventory.WeaponTableCraftingSlot;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Container to handle crafting in the hunter weapon crafting table
 */
public class WeaponTableContainer extends RecipeBookMenu<CraftingContainer> {
    private final ContainerLevelAccess worldPos;
    private final @NotNull HunterPlayer hunterPlayer;
    private final @NotNull Player player;
    private final CraftingContainer craftMatrix = new CraftingContainer(this, 4, 4);
    private final ResultContainer craftResult = new ResultContainer();
    private final BooleanDataSlot missingLava = new BooleanDataSlot();

    public WeaponTableContainer(int id, @NotNull Inventory playerInventory, ContainerLevelAccess worldPosCallable) {
        super(ModContainer.WEAPON_TABLE.get(), id);
        this.worldPos = worldPosCallable;
        this.hunterPlayer = HunterPlayer.get(playerInventory.player);
        this.player = playerInventory.player;
        this.addSlot(new WeaponTableCraftingSlot(playerInventory.player, craftMatrix, craftResult, 0, 144, 46, worldPosCallable));

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 4, 34 + j * 19, 16 + i * 19));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 18 + i1 * 18, 107 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 18 + l * 18, 165));
        }

        this.slotsChanged(this.craftMatrix);
        this.addDataSlot(missingLava);
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, @NotNull Slot slotIn) {
        return slotIn.container != this.craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public void clearCraftingContent() {
        craftMatrix.clearContent();
        craftResult.clearContent();
    }

    @Override
    public void fillCraftSlotsStackedContents(@NotNull StackedContents recipeItemHelper) {
        craftMatrix.fillStackedContents(recipeItemHelper);
    }

    @Override
    public int getGridHeight() {
        return craftMatrix.getHeight();
    }

    @Override
    public int getGridWidth() {
        return craftMatrix.getWidth();
    }

    @NotNull
    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int p_150635_) {
        return p_150635_ != getResultSlotIndex();
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handlePlacement(boolean shouldPlaceAll, @NotNull Recipe<?> recipe, @NotNull ServerPlayer serverPlayer) {
        new NBTServerPlaceRecipe<>(this).recipeClicked(serverPlayer, (Recipe<CraftingContainer>) recipe, shouldPlaceAll);
    }

    @NotNull
    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        return Lists.newArrayList(RecipeBookCategories.UNKNOWN);
    }

    @Override
    public int getSize() {
        return 17;
    }

    public boolean hasLava() {
        return worldPos.evaluate(((world, blockPos) -> world.getBlockState(blockPos).getValue(WeaponTableBlock.LAVA) > 0), false);
    }

    @NotNull
    public ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemStackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemStackCopy = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 17, 53, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemStackCopy);
            } else if (index >= 17 && index < 44) {
                if (!this.moveItemStackTo(itemstack1, 44, 53, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 44 && index < 53) {
                if (!this.moveItemStackTo(itemstack1, 17, 44, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 17, 53, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStackCopy.getCount() == itemstack1.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemStackCopy;
    }

    /**
     * @return if there's a recipe available for the given setup, which requires more lava
     */
    public boolean isMissingLava() {
        return missingLava.getB();
    }

    @Override
    public boolean recipeMatches(@NotNull Recipe<? super CraftingContainer> recipeIn) {
        return recipeIn.matches(craftMatrix, this.player.level);
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        this.worldPos.execute((world, pos) -> {
            this.clearContainer(playerIn, craftMatrix);
            for (int i = 0; i < this.craftMatrix.getContainerSize(); ++i) {
                ItemStack itemstack = this.craftMatrix.removeItemNoUpdate(i);

                if (!itemstack.isEmpty()) {
                    playerIn.drop(itemstack, false);
                }
            }
            missingLava.set(false);
        });
    }

    @Override
    public void slotsChanged(@NotNull Container inventoryIn) {
        this.worldPos.execute((world, pos) -> slotChangedCraftingGrid(world, this.player, this.hunterPlayer, this.craftMatrix, this.craftResult));
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(this.worldPos, playerIn, ModBlocks.WEAPON_TABLE.get());
    }

    private void slotChangedCraftingGrid(@NotNull Level worldIn, Player playerIn, @NotNull HunterPlayer hunter, @NotNull CraftingContainer craftMatrixIn, @NotNull ResultContainer craftResultIn) {
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayer serverPlayer) {
            Optional<IWeaponTableRecipe> optional = worldIn.getServer() == null ? Optional.empty() : worldIn.getServer().getRecipeManager().getRecipeFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get(), craftMatrixIn, worldIn);
            this.missingLava.set(false);
            craftResultIn.setItem(0, ItemStack.EMPTY);
            if (optional.isPresent()) {
                IWeaponTableRecipe recipe = optional.get();
                if ((craftResultIn.setRecipeUsed(worldIn, serverPlayer, recipe) || ModList.get().isLoaded("fastbench")) && recipe.getRequiredLevel() <= hunter.getLevel() && Helper.areSkillsEnabled(hunter.getSkillHandler(), recipe.getRequiredSkills())) {
                    this.worldPos.execute((world, pos) -> {
                        if (world.getBlockState(pos).getValue(WeaponTableBlock.LAVA) >= recipe.getRequiredLavaUnits()) {
                            craftResultIn.setItem(0, recipe.assemble(craftMatrixIn));
                        } else {
                            this.missingLava.set(true);
                        }
                    });
                }
            }
            broadcastChanges();
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, craftResultIn.getItem(0)));
        }
    }

    public static class Factory implements IContainerFactory<WeaponTableContainer> {

        @Override
        public @NotNull WeaponTableContainer create(int windowId, @NotNull Inventory inv, @NotNull FriendlyByteBuf data) {
            BlockPos pos = data.readBlockPos();
            return new WeaponTableContainer(windowId, inv, ContainerLevelAccess.create(inv.player.level, pos));
        }
    }

    /**
     * no good implementation but since it's usage is limited a functional fix for ignoring the nbt data for itemstacks
     * TODO 1.19 recheck
     */
    static class NBTServerPlaceRecipe<C extends Container> extends ServerPlaceRecipe<C> {

        public NBTServerPlaceRecipe(@NotNull RecipeBookMenu<C> p_135431_) {
            super(p_135431_);
        }

        /**
         * same as super method but {@link #findSlotMatchingUnusedItem(net.minecraft.world.item.ItemStack)} is called instead of {@link net.minecraft.world.entity.player.Inventory#findSlotMatchingUnusedItem(net.minecraft.world.item.ItemStack)}
         */
        @Override
        protected void moveItemToGrid(Slot p_135439_, ItemStack p_135440_) {
            int i = findSlotMatchingUnusedItem(p_135440_);
            if (i != -1) {
                ItemStack itemstack = this.inventory.getItem(i).copy();
                if (!itemstack.isEmpty()) {
                    if (itemstack.getCount() > 1) {
                        this.inventory.removeItem(i, 1);
                    } else {
                        this.inventory.removeItemNoUpdate(i);
                    }

                    itemstack.setCount(1);
                    if (p_135439_.getItem().isEmpty()) {
                        p_135439_.set(itemstack);
                    } else {
                        p_135439_.getItem().grow(1);
                    }

                }
            }
        }

        /**
         * Copied from {@link net.minecraft.world.entity.player.Inventory#findSlotMatchingUnusedItem(net.minecraft.world.item.ItemStack)}
         * but nbt check removed
         */
        public int findSlotMatchingUnusedItem(@NotNull ItemStack p_36044_) {
            for (int i = 0; i < this.inventory.items.size(); ++i) {
                ItemStack itemstack = this.inventory.items.get(i);
                //    do not check for nbt tag here    //
                if (!this.inventory.items.get(i).isEmpty() && p_36044_.is(itemstack.getItem()) && !this.inventory.items.get(i).isDamaged() && !itemstack.isEnchanted() && !itemstack.hasCustomHoverName()) {
                    return i;
                }
            }

            return -1;
        }
    }
}
