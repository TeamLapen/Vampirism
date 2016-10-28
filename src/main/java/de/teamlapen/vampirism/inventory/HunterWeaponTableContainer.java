package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Container to handle crafting in the hunter weapon crafting table
 */
public class HunterWeaponTableContainer extends Container {
    private final World world;
    private final BlockPos pos;
    private final HunterPlayer hunterPlayer;
    private InventoryCrafting craftMatrix = new InventoryCrafting(this, 4, 4);
    private IInventory craftResult = new InventoryCraftResult();

    public HunterWeaponTableContainer(InventoryPlayer playerInventory, World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.hunterPlayer = HunterPlayer.get(playerInventory.player);
        this.addSlotToContainer(new HunterWeaponTableCraftingSlot(playerInventory.player, world, pos, craftMatrix, craftResult, 0, 144, 46));

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 4, 34 + j * 19, 16 + i * 19));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 18 + i1 * 18, 107 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot(playerInventory, l, 18 + l * 18, 165));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
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
        IHunterWeaponRecipe recipe = HunterWeaponCraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.world, hunterPlayer.getLevel(), hunterPlayer.getSkillHandler(), 100);
        if (recipe != null) {
            IBlockState blockState = world.getBlockState(pos);
            if (blockState.getBlock() instanceof BlockWeaponTable) {
                return blockState.getValue(BlockWeaponTable.LAVA) < recipe.getRequiredLavaUnits();
            }
        }
        return false;

    }

    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);

                if (itemstack != null) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }



    public void onCraftMatrixChanged(IInventory inventoryIn) {
        int lava = 0;
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BlockWeaponTable) {
            lava = blockState.getValue(BlockWeaponTable.LAVA);
        }
        this.craftResult.setInventorySlotContents(0, HunterWeaponCraftingManager.getInstance().findMatchingRecipeResult(this.craftMatrix, this.world, hunterPlayer.getLevel(), hunterPlayer.getSkillHandler(),lava));
    }

    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 17, 53, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 17 && index < 44) {
                if (!this.mergeItemStack(itemstack1, 44, 53, false)) {
                    return null;
                }
            } else if (index >= 44 && index < 53) {
                if (!this.mergeItemStack(itemstack1, 17, 44, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 17, 53, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }
}
