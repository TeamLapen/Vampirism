package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Result slot for the hunter weapon crafting table
 */
public class HunterWeaponTableCraftingSlot extends Slot {
    private final PlayerEntity player;
    private final World world;
    private final BlockPos pos;
    private final CraftingInventory craftMatrix;
    private int amountCrafted = 0;

    public HunterWeaponTableCraftingSlot(PlayerEntity player, World world, BlockPos pos, CraftingInventory craftingInventory, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = player;
        this.craftMatrix = craftingInventory;
        this.world = world;
        this.pos = pos;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.amountCrafted += Math.min(amount, this.getStack().getCount());

        }
        return super.decrStackSize(amount);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return false;
    }


    @Override
    public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
        this.onCrafting(stack);
        int lava = 0;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BlockWeaponTable) {
            lava = blockState.get(BlockWeaponTable.LAVA);
        }
        HunterPlayer hunterPlayer = HunterPlayer.get(playerIn);
        IWeaponTableRecipe recipe = findMatchingRecipe(playerIn, hunterPlayer, lava);
        if (recipe != null && recipe.getRequiredLavaUnits() > 0) {
            lava = Math.max(0, lava - recipe.getRequiredLavaUnits());
            if (blockState.getBlock() instanceof BlockWeaponTable) {
                world.setBlockState(pos, blockState.with(BlockWeaponTable.LAVA, lava));
            }
        }
        NonNullList<ItemStack> remaining = recipe == null ? playerIn.world.getRecipeManager().getRemainingItems(this.craftMatrix, playerIn.world, ModRecipes.WEAPONTABLE_CRAFTING_TYPE) : recipe.getRemainingItems(this.craftMatrix);

        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
            ItemStack itemstack1 = remaining.get(i);

            if (!itemstack.isEmpty()) {
                this.craftMatrix.decrStackSize(i, 1);
                itemstack = this.craftMatrix.getStackInSlot(i);
            }

            if (!itemstack1.isEmpty()) {
                if (itemstack.isEmpty()) {
                    this.craftMatrix.setInventorySlotContents(i, itemstack1);
                } else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
                    itemstack1.grow(itemstack.getCount());
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
        //TODO playerIn.addStat(Achievements.weaponTable);
        return stack;
    }

    protected IWeaponTableRecipe findMatchingRecipe(PlayerEntity playerIn, IFactionPlayer<?> factionPlayer, int lava) {
        IWeaponTableRecipe recipe = (IWeaponTableRecipe) playerIn.getEntityWorld().getServer().getRecipeManager().getRecipe(this.craftMatrix, playerIn.getEntityWorld(), ModRecipes.WEAPONTABLE_CRAFTING_TYPE);
        if (factionPlayer.getLevel() >= recipe.getRequiredLevel() && lava >= recipe.getRequiredLavaUnits() && Helper.areSkillsEnabled(factionPlayer.getSkillHandler(), recipe.getRequiredSkills())) {
            return recipe;
        }
        return null;
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
