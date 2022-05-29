package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.client.gui.recipebook.WeaponTableRecipePlacer;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.inventory.inventory.WeaponTableCraftingSlot;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * Container to handle crafting in the hunter weapon crafting table
 */
public class WeaponTableContainer extends RecipeBookContainer<CraftingInventory> {
    private final IWorldPosCallable worldPos;
    private final HunterPlayer hunterPlayer;
    private final PlayerEntity player;
    private final CraftingInventory craftMatrix = new CraftingInventory(this, 4, 4);
    private final CraftResultInventory craftResult = new CraftResultInventory();
    private boolean missingLava = false;
    private boolean prevMissingLava = false;

    public WeaponTableContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
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
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (IContainerListener icontainerlistener : this.containerListeners) {
            if (this.prevMissingLava != this.missingLava) {
                icontainerlistener.setContainerData(this, 0, missingLava ? 1 : 0);
            }

        }
        this.prevMissingLava = missingLava;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.container != this.craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public void clearCraftingContent() {
        craftMatrix.clearContent();
        craftResult.clearContent();
    }

    @Override
    public void fillCraftSlotsStackedContents(@Nonnull RecipeItemHelper recipeItemHelper) {
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

    @Override
    public RecipeBookCategory getRecipeBookType() {
        return RecipeBookCategory.CRAFTING;
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handlePlacement(boolean shouldPlaceAll, @Nonnull IRecipe<?> recipe, @Nonnull ServerPlayerEntity serverPlayer) {
        new WeaponTableRecipePlacer<>(this).recipeClicked(serverPlayer, (IRecipe<CraftingInventory>) recipe, shouldPlaceAll);
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
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemStackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
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
        return missingLava;

    }

    @Override
    public boolean recipeMatches(IRecipe<? super CraftingInventory> recipeIn) {
        return recipeIn.matches(craftMatrix, this.player.level);
    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        this.worldPos.execute((world, pos) -> {
            this.clearContainer(playerIn, world, craftMatrix);
            for (int i = 0; i < this.craftMatrix.getContainerSize(); ++i) {
                ItemStack itemstack = this.craftMatrix.removeItemNoUpdate(i);

                if (!itemstack.isEmpty()) {
                    playerIn.drop(itemstack, false);
                }
            }
            missingLava = false;
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setData(int id, int data) {
        if (id == 0) {
            missingLava = data != 0;
        }
    }

    @Override
    public void slotsChanged(IInventory inventoryIn) {
        this.worldPos.execute((world, pos) -> {
            slotChangedCraftingGrid(world, this.player, this.hunterPlayer, this.craftMatrix, this.craftResult);
        });
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity playerIn) {
        return stillValid(this.worldPos, playerIn, ModBlocks.WEAPON_TABLE.get());
    }

    private void slotChangedCraftingGrid(World worldIn, PlayerEntity playerIn, HunterPlayer hunter, CraftingInventory craftMatrixIn, CraftResultInventory craftResultIn) {
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity entityplayermp = (ServerPlayerEntity) playerIn;
            Optional<IWeaponTableRecipe> optional = worldIn.getServer() == null ? Optional.empty() : worldIn.getServer().getRecipeManager().getRecipeFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE, craftMatrixIn, worldIn);
            this.missingLava = false;
            craftResultIn.setItem(0, ItemStack.EMPTY);
            if (optional.isPresent()) {
                IWeaponTableRecipe recipe = optional.get();
                if ((craftResultIn.setRecipeUsed(worldIn, entityplayermp, recipe) || ModList.get().isLoaded("fastbench")) && recipe.getRequiredLevel() <= hunter.getLevel() && Helper.areSkillsEnabled(hunter.getSkillHandler(), recipe.getRequiredSkills())) {
                    this.worldPos.execute((world, pos) -> {
                        if (world.getBlockState(pos).getValue(WeaponTableBlock.LAVA) >= recipe.getRequiredLavaUnits()) {
                            craftResultIn.setItem(0, recipe.assemble(craftMatrixIn));
                        } else {
                            this.missingLava = true;
                        }
                    });
                }
            }
            entityplayermp.connection.send(new SSetSlotPacket(this.containerId, 0, craftResultIn.getItem(0)));
        }
    }

    public static class Factory implements IContainerFactory<WeaponTableContainer> {

        @Override
        public WeaponTableContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            BlockPos pos = data.readBlockPos();
            return new WeaponTableContainer(windowId, inv, IWorldPosCallable.create(inv.player.level, pos));
        }
    }
}
