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
        super(ModContainer.weapon_table, id);
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

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void func_217056_a(boolean shouldPlaceAll, @Nonnull IRecipe<?> recipe, @Nonnull ServerPlayerEntity serverPlayer) {
        new WeaponTableRecipePlacer<>(this).place(serverPlayer, (IRecipe<CraftingInventory>)recipe, shouldPlaceAll);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPos, playerIn, ModBlocks.weapon_table);
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }

    @Override
    public void clear() {
        craftMatrix.clear();
        craftResult.clear();
    }

    @Override
    public void fillStackedContents(@Nonnull RecipeItemHelper recipeItemHelper) {
        craftMatrix.fillStackedContents(recipeItemHelper);
    }

    @Override
    public int getHeight() {
        return craftMatrix.getHeight();
    }

    @Override
    public int getOutputSlot() {
        return 0;
    }

    @Override
    public int getSize() {
        return 17;
    }

    @Override
    public int getWidth() {
        return craftMatrix.getWidth();
    }

    public boolean hasLava() {
        return worldPos.applyOrElse(((world, blockPos) -> world.getBlockState(blockPos).get(WeaponTableBlock.LAVA) > 0), false);
    }

    /**
     * @return if there's a recipe available for the given setup, which requires more lava
     */
    public boolean isMissingLava() {
        return missingLava;

    }

    @Override
    public boolean matches(IRecipe<? super CraftingInventory> recipeIn) {
        return recipeIn.matches(craftMatrix, this.player.world);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.worldPos.consume((world, pos) -> {
            this.clearContainer(playerIn, world, craftMatrix);
            for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
                ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);

                if (!itemstack.isEmpty()) {
                    playerIn.dropItem(itemstack, false);
                }
            }
            missingLava = false;
        });
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.worldPos.consume((world, pos) -> {
            slotChangedCraftingGrid(world, this.player, this.hunterPlayer, this.craftMatrix, this.craftResult);
        });
    }

    @Nonnull
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemStackCopy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemStackCopy = itemstack1.copy();
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 17, 53, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemStackCopy);
            } else if (index >= 17 && index < 44) {
                if (!this.mergeItemStack(itemstack1, 44, 53, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 44 && index < 53) {
                if (!this.mergeItemStack(itemstack1, 17, 44, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 17, 53, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemStackCopy.getCount() == itemstack1.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemStackCopy;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener icontainerlistener : this.listeners) {
            if (this.prevMissingLava != this.missingLava) {
                icontainerlistener.sendWindowProperty(this, 0, missingLava ? 1 : 0);
            }

        }
        this.prevMissingLava = missingLava;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            missingLava = data != 0;
        }
    }

    private void slotChangedCraftingGrid(World worldIn, PlayerEntity playerIn, HunterPlayer hunter, CraftingInventory craftMatrixIn, CraftResultInventory craftResultIn) {
        if (!worldIn.isRemote) {
            ServerPlayerEntity entityplayermp = (ServerPlayerEntity) playerIn;
            Optional<IWeaponTableRecipe> optional = worldIn.getServer() == null ? Optional.empty() : worldIn.getServer().getRecipeManager().getRecipe(ModRecipes.WEAPONTABLE_CRAFTING_TYPE, craftMatrixIn, worldIn);
            this.missingLava = false;
            craftResultIn.setInventorySlotContents(0, ItemStack.EMPTY);
            if (optional.isPresent()) {
                IWeaponTableRecipe recipe = optional.get();
                if (craftResultIn.canUseRecipe(worldIn, entityplayermp, recipe) && recipe.getRequiredLevel() <= hunter.getLevel() && Helper.areSkillsEnabled(hunter.getSkillHandler(), recipe.getRequiredSkills())) {
                    this.worldPos.consume((world, pos) -> {
                        if (world.getBlockState(pos).get(WeaponTableBlock.LAVA) >= recipe.getRequiredLavaUnits()) {
                            craftResultIn.setInventorySlotContents(0, recipe.getCraftingResult(craftMatrixIn));
                        } else {
                            this.missingLava = true;
                        }
                    });
                }
            }
            entityplayermp.connection.sendPacket(new SSetSlotPacket(this.windowId, 0, craftResultIn.getStackInSlot(0)));
        }
    }

    @Nonnull
    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        return Lists.newArrayList(RecipeBookCategories.UNKNOWN);
    }

    @Override
    public RecipeBookCategory func_241850_m() {
        return RecipeBookCategory.CRAFTING;
    }

    public static class Factory implements IContainerFactory<WeaponTableContainer> {

        @Override
        public WeaponTableContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            BlockPos pos = data.readBlockPos();
            return new WeaponTableContainer(windowId, inv, IWorldPosCallable.of(inv.player.world, pos));
        }
    }
}
