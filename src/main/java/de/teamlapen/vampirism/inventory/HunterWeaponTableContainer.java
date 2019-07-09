package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Container to handle crafting in the hunter weapon crafting table
 */
public class HunterWeaponTableContainer extends Container {
    private final World world;
    private final BlockPos pos;
    private final HunterPlayer hunterPlayer;
    private CraftingInventory craftMatrix = new CraftingInventory(this, 4, 4);
    private CraftResultInventory craftResult = new CraftResultInventory();
    private boolean missingLava = false;

    public HunterWeaponTableContainer(PlayerInventory playerInventory, World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.hunterPlayer = HunterPlayer.get(playerInventory.player);
        this.addSlot(new HunterWeaponTableCraftingSlot(playerInventory.player, world, pos, craftMatrix, craftResult, 0, 144, 46));

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

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }

    /**
     * Checks if there's a recipe available for the given setup, which requires more lava
     *
     * @return
     */
    public boolean isMissingLava() {
        return missingLava;

    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote) {
            for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
                ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);

                if (!itemstack.isEmpty()) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
        
        missingLava = false;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        slotChangedCraftingGrid(this.world, this.hunterPlayer.getRepresentingPlayer(), this.craftMatrix, this.craftResult);
    }
    
    @Nonnull
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 17, 53, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
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

            if (itemstack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack.getCount() == itemstack1.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    protected void slotChangedCraftingGrid(World worldIn, PlayerEntity playerIn, IInventory craftMatrixIn, CraftResultInventory craftResultIn) {
        if (!worldIn.isRemote) {
            ServerPlayerEntity entityplayermp = (ServerPlayerEntity) playerIn;
            HunterPlayer hunter = HunterPlayer.get(playerIn);
            ItemStack itemstack = ItemStack.EMPTY;
            IRecipe irecipe = worldIn.getServer().getRecipeManager().getRecipe(craftMatrixIn, worldIn, ModRecipes.WEAPONTABLE_CRAFTING_TYPE);
            boolean working = true;
            missingLava = false;
            if (irecipe instanceof IWeaponTableRecipe && craftResultIn.canUseRecipe(worldIn, entityplayermp, irecipe)) {
                IWeaponTableRecipe recipe = (IWeaponTableRecipe) irecipe;
                if (recipe.getRequiredLevel() <= hunter.getLevel() && Helper.areSkillsEnabled(hunterPlayer.getSkillHandler(), recipe.getRequiredSkills())) {
                    if (recipe.getRequiredLavaUnits() >= worldIn.getBlockState(pos).get(BlockWeaponTable.LAVA)) { //TODO LAVA at BlockPos, BlockWeaponTable.Propert
                        itemstack = irecipe.getCraftingResult(craftMatrixIn);

                    } else {
                        missingLava = true;
                    }
                }
            }
            craftResultIn.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SSetSlotPacket(this.windowId, 0, itemstack));
        }
    }
}
