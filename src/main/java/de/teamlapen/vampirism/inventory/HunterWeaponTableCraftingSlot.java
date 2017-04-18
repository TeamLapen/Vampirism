package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import de.teamlapen.vampirism.core.Achievements;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Result slot for the hunter weapon crafting table
 */
public class HunterWeaponTableCraftingSlot extends Slot {
    private final EntityPlayer player;
    private final World world;
    private final BlockPos pos;
    private final InventoryCrafting craftMatrix;
    private int amountCrafted = 0;

    public HunterWeaponTableCraftingSlot(EntityPlayer player, World world, BlockPos pos, InventoryCrafting craftingInventory, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = player;
        this.craftMatrix = craftingInventory;
        this.world = world;
        this.pos = pos;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.amountCrafted += Math.min(amount, ItemStackUtil.getCount(this.getStack()));

        }
        return super.decrStackSize(amount);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return false;
    }


    @Override
    public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
        this.onCrafting(stack);
        int lava = 0;
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BlockWeaponTable) {
            lava = blockState.getValue(BlockWeaponTable.LAVA);
        }
        HunterPlayer hunterPlayer = HunterPlayer.get(playerIn);
        IHunterWeaponRecipe recipe = HunterWeaponCraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, playerIn.getEntityWorld(), hunterPlayer.getLevel(), hunterPlayer.getSkillHandler(), lava);
        if (recipe != null && recipe.getRequiredLavaUnits() > 0) {
            lava = Math.max(0, lava - recipe.getRequiredLavaUnits());
            if (blockState.getBlock() instanceof BlockWeaponTable) {
                world.setBlockState(pos, blockState.withProperty(BlockWeaponTable.LAVA, lava));
            }
        }
        NonNullList<ItemStack> remaining = recipe == null ? HunterWeaponCraftingManager.getInstance().getRemainingItems(this.craftMatrix, playerIn.getEntityWorld(), hunterPlayer.getLevel(), hunterPlayer.getSkillHandler(), lava) : recipe.getRemainingItems(this.craftMatrix);

        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
            ItemStack itemstack1 = remaining.get(i);

            if (!ItemStackUtil.isEmpty(itemstack)) {
                this.craftMatrix.decrStackSize(i, 1);
                itemstack = this.craftMatrix.getStackInSlot(i);
            }

            if (!ItemStackUtil.isEmpty(itemstack1)) {
                if (ItemStackUtil.isEmpty(itemstack)) {
                    this.craftMatrix.setInventorySlotContents(i, itemstack1);
                } else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
                    ItemStackUtil.grow(itemstack1, ItemStackUtil.getCount(itemstack));
                    this.craftMatrix.setInventorySlotContents(i, itemstack1);
                } else if (!this.player.inventory.addItemStackToInventory(itemstack1)) {
                    this.player.dropItem(itemstack1, false);
                }
            }
        }
        if (recipe != null && !world.isRemote) {
            //Play anvil sound
            world.playEvent(1030, pos, 0);
        }
        playerIn.addStat(Achievements.weaponTable);
        return stack;
    }

    @Override
    protected void onCrafting(ItemStack stack) {
        if (this.amountCrafted > 0) {
            stack.onCrafting(this.player.getEntityWorld(), this.player, this.amountCrafted);
        }

        this.amountCrafted = 0;
    }

    @Override
    protected void onCrafting(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }
}
