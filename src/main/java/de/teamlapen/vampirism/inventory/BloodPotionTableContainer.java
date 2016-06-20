package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.Achievements;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.potion.blood.BloodPotions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Table to create blood potions
 */
public class BloodPotionTableContainer extends Container {
    private static final InventorySlot.IItemSelector bloodfilter = new InventorySlot.IItemSelector() {
        @Override
        public boolean isItemAllowed(@Nonnull ItemStack item) {
            return ModItems.vampireBlood.equals(item.getItem());
        }
    };
    private final BlockPos pos;
    private final HunterPlayer hunterPlayer;
    private final World world;
    private final int max_crafting_time;
    private final boolean portable;
    private final IInventory inventory = new InventoryBasic("vampirism.blood_potion_table", false, 4);
    private int craftingTimer = 0;
    private int prevCraftingTimer = 0;
    public BloodPotionTableContainer(InventoryPlayer playerInventory, BlockPos pos, World world) {

        this.pos = pos;
        this.hunterPlayer = HunterPlayer.get(playerInventory.player);
        this.world = world;
        portable = !ModBlocks.bloodPotionTable.equals(world.getBlockState(pos).getBlock());
        int crafting_time = portable ? 500 : 250;
        if (hunterPlayer.getSkillHandler().isSkillEnabled(HunterSkills.bloodPotion_fasterCrafting)) {
            crafting_time /= 2;
        }
        this.max_crafting_time = crafting_time;

        this.addSlotToContainer(new PotionSlot(inventory, 0, 115, 55));
        this.addSlotToContainer(new PotionSlot(inventory, 1, 137, 55));
        this.addSlotToContainer(new InventoryContainer.FilterSlot(inventory, 2, 126, 14, new InventorySlot.IItemSelector() {
            @Override
            public boolean isItemAllowed(ItemStack item) {
                return ModItems.itemGarlic.equals(item.getItem());
            }
        }));
        this.addSlotToContainer(new Slot(inventory, 3, 101, 22));

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    /**
     * @return If requirements met and not currently crafting
     */
    public boolean canCurrentlyStartCrafting() {
        return craftingTimer <= 0 && areRequirementsMet();

    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icontainerlistener = this.listeners.get(i);

            if (this.prevCraftingTimer != this.craftingTimer) {
                icontainerlistener.sendProgressBarUpdate(this, 0, craftingTimer);
            }

        }
        this.prevCraftingTimer = craftingTimer;
    }

    /**
     * @return The current crafting progress in percent
     */
    public float getCraftingPercentage() {
        return craftingTimer == 0 ? 0 : (1F - craftingTimer / (float) max_crafting_time);
    }

    @SideOnly(Side.CLIENT)
    public
    @Nullable
    List<String> getLocalizedCraftingHint() {
        ItemStack extra = inventory.getStackInSlot(3);
        if (extra == null) return null;
        if (!hunterPlayer.getSkillHandler().isSkillEnabled(HunterSkills.bloodPotion_categoryHint)) return null;
        List<String> hints = BloodPotions.getLocalizedCategoryHint(extra);
        if (hints.isEmpty()) {
            hints.add(UtilLib.translateToLocal("text.vampirism.blood_potion.any_effect"));
        } else {
            hints.add(0, UtilLib.translateToLocal("text.vampirism.blood_potion.might_cause"));
        }
        return hints;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote) {

            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.inventory.removeStackFromSlot(i);

                if (itemstack != null) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }

    /**
     * Called when the crafting button is clicked server side
     */
    public void onCraftingClicked() {
        if (canCurrentlyStartCrafting()) {
            craftingTimer = max_crafting_time;
        }

    }

    /**
     * Called via a player living update event every tick serverside while the container is opened.
     */
    public void tick() {
        if (craftingTimer > 0) {
            craftingTimer--;
            if (craftingTimer == 0) {
                onCraftingTimerFinished();
            }
        }
    }

    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index >= 0 && index < 4) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return null;
                }

            } else if (index >= 3 && index < 30) {
                if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                    return null;
                }
            } else if (index >= 30 && index < 39) {
                if (!this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
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

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0 && data >= 0 && data <= max_crafting_time) {
            craftingTimer = data;
        }
    }

    /**
     * @return if all required items are in the container
     */
    private boolean areRequirementsMet() {
        ItemStack garlic = inventory.getStackInSlot(2);
        if (garlic == null || !ModItems.itemGarlic.equals(garlic.getItem())) return false;
        boolean bottle = false;
        ItemStack bottle1 = inventory.getStackInSlot(0);
        ItemStack bottle2 = inventory.getStackInSlot(1);
        if (bottle1 != null && bottle1.getItem().equals(ModItems.vampireBlood)) bottle = true;
        if (bottle2 != null && bottle2.getItem().equals(ModItems.vampireBlood)) bottle = true;
        return bottle;
    }

    /**
     * Execute the crafting as long as the requirements are still met
     */
    private void onCraftingTimerFinished() {
        if (!areRequirementsMet()) return;
        ItemStack extraItem = inventory.getStackInSlot(3);
        if (extraItem != null) {
            extraItem = extraItem.copy();
            extraItem.stackSize = 1;
            inventory.decrStackSize(3, 1);
        }
        inventory.decrStackSize(2, 1);//Reduce garlic
        ItemStack bottle1 = inventory.getStackInSlot(0);
        ItemStack bottle2 = inventory.getStackInSlot(1);
        if (bottle1 != null && bottle1.getItem().equals(ModItems.vampireBlood)) {
            bottle1 = new ItemStack(ModItems.bloodPotion);
            BloodPotions.chooseAndAddEffects(bottle1, hunterPlayer, extraItem);
        }
        if (bottle2 != null && bottle2.getItem().equals(ModItems.vampireBlood)) {
            bottle2 = new ItemStack(ModItems.bloodPotion);
            BloodPotions.chooseAndAddEffects(bottle2, hunterPlayer, extraItem);
        }
        inventory.setInventorySlotContents(0, bottle1);
        inventory.setInventorySlotContents(1, bottle2);
        hunterPlayer.getRepresentingPlayer().addStat(Achievements.bloodTable);
    }

    private class PotionSlot extends InventoryContainer.FilterSlot {


        private PotionSlot(IInventory inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition, bloodfilter);
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}
