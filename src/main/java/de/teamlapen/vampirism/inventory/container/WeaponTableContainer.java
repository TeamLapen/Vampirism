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

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * Container to handle crafting in the hunter weapon crafting table
 */
public class WeaponTableContainer extends RecipeBookMenu<CraftingContainer> {
    private final ContainerLevelAccess worldPos;
    private final HunterPlayer hunterPlayer;
    private final Player player;
    private final CraftingContainer craftMatrix = new CraftingContainer(this, 4, 4);
    private final ResultContainer craftResult = new ResultContainer();
    private final BooleanDataSlot missingLava = new BooleanDataSlot();

    public WeaponTableContainer(int id, Inventory playerInventory, ContainerLevelAccess worldPosCallable) {
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
    public boolean canTakeItemForPickAll(@Nonnull ItemStack stack, Slot slotIn) {
        return slotIn.container != this.craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public void clearCraftingContent() {
        craftMatrix.clearContent();
        craftResult.clearContent();
    }

    @Override
    public void fillCraftSlotsStackedContents(@Nonnull StackedContents recipeItemHelper) {
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

    @Nonnull
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
    public void handlePlacement(boolean shouldPlaceAll, @Nonnull Recipe<?> recipe, @Nonnull ServerPlayer serverPlayer) {
        new ServerPlaceRecipe<>(this).recipeClicked(serverPlayer, (Recipe<CraftingContainer>) recipe, shouldPlaceAll);
    }

    @Nonnull
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

    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player playerIn, int index) {
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
    public boolean recipeMatches(Recipe<? super CraftingContainer> recipeIn) {
        return recipeIn.matches(craftMatrix, this.player.level);
    }

    @Override
    public void removed(@Nonnull Player playerIn) {
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
    public void slotsChanged(@Nonnull Container inventoryIn) {
        this.worldPos.execute((world, pos) -> slotChangedCraftingGrid(world, this.player, this.hunterPlayer, this.craftMatrix, this.craftResult));
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return stillValid(this.worldPos, playerIn, ModBlocks.WEAPON_TABLE.get());
    }

    private void slotChangedCraftingGrid(Level worldIn, Player playerIn, HunterPlayer hunter, CraftingContainer craftMatrixIn, ResultContainer craftResultIn) {
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
        public WeaponTableContainer create(int windowId, Inventory inv, FriendlyByteBuf data) {
            BlockPos pos = data.readBlockPos();
            return new WeaponTableContainer(windowId, inv, ContainerLevelAccess.create(inv.player.level, pos));
        }
    }
}
