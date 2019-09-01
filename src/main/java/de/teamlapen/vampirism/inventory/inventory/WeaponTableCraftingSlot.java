package de.teamlapen.vampirism.inventory.inventory;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Result slot for the hunter weapon crafting table
 */
public class WeaponTableCraftingSlot extends Slot {
    private final PlayerEntity player;
    private final IWorldPosCallable worldPos;
    private final CraftingInventory craftMatrix;
    private int amountCrafted = 0;

    public WeaponTableCraftingSlot(PlayerEntity player, CraftingInventory craftingInventory, IInventory inventoryIn, int index, int xPosition, int yPosition, IWorldPosCallable worldPosCallable) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = player;
        this.craftMatrix = craftingInventory;
        this.worldPos = worldPosCallable;
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
        final int lava = worldPos.applyOrElse(((world, blockPos) -> {
            if (world.getBlockState(blockPos).getBlock() instanceof WeaponTableBlock) {
                return world.getBlockState(blockPos).get(WeaponTableBlock.LAVA);
            }
            return 0;
        }), 0);
        final HunterPlayer hunterPlayer = HunterPlayer.get(playerIn);
        final IWeaponTableRecipe recipe = findMatchingRecipe(playerIn, hunterPlayer, lava);
        if (recipe != null && recipe.getRequiredLavaUnits() > 0) {
            worldPos.consume(((world, pos) -> {
                int remainingLava = Math.max(0, lava - recipe.getRequiredLavaUnits());
                if (world.getBlockState(pos).getBlock() instanceof WeaponTableBlock) {
                    world.setBlockState(pos, world.getBlockState(pos).with(WeaponTableBlock.LAVA, remainingLava));
                }
            }));
        }
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
        NonNullList<ItemStack> remaining = playerIn.world.getRecipeManager().getRecipeNonNull(ModRecipes.WEAPONTABLE_CRAFTING_TYPE, this.craftMatrix, playerIn.world);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
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
            worldPos.consume(((world, pos) -> {
                if (recipe != null && !world.isRemote) {
                    //Play anvil sound
                    world.playEvent(1030, pos, 0);
                }
            }));
        playerIn.addStat(ModStats.weapon_table);
        return stack;
    }

    protected IWeaponTableRecipe findMatchingRecipe(PlayerEntity playerIn, IFactionPlayer<?> factionPlayer, int lava) {
        Optional<IWeaponTableRecipe> optional = playerIn.getEntityWorld().getRecipeManager().getRecipe(ModRecipes.WEAPONTABLE_CRAFTING_TYPE, this.craftMatrix, playerIn.getEntityWorld());
        if (optional.isPresent()) {
            IWeaponTableRecipe recipe = optional.get();
            if (factionPlayer.getLevel() >= recipe.getRequiredLevel() && lava >= recipe.getRequiredLavaUnits() && Helper.areSkillsEnabled(factionPlayer.getSkillHandler(), recipe.getRequiredSkills())) {
                return recipe;
            }
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
