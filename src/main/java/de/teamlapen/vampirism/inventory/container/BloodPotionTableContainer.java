package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.potion.blood.BloodPotions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Table to create blood potions
 */
public class BloodPotionTableContainer extends InventoryContainer {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Ingredient.fromItems(ModItems.vampire_blood_bottle), 115, 55, 1), new SelectorInfo(Ingredient.fromItems(ModItems.vampire_blood_bottle), 137, 55, 1), new SelectorInfo(Ingredient.fromItems(ModItems.item_garlic), 126, 14), new SelectorInfo(Ingredient.fromItems(ModItems.item_garlic, ModItems.vampire_blood_bottle), 101, 22, true)};
    private final HunterPlayer hunterPlayer;
    private final int max_crafting_time;
    private final boolean portable;
    private int craftingTimer = 0;
    private int prevCraftingTimer = 0;

    @Deprecated
    public BloodPotionTableContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public BloodPotionTableContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosIn) {
        super(ModContainer.blood_potion_table, id, worldPosIn, SELECTOR_INFOS);
        this.hunterPlayer = HunterPlayer.get(playerInventory.player);
        portable = worldPos.applyOrElse(((world, blockPos) -> !ModBlocks.blood_potion_table.equals(world.getBlockState(blockPos).getBlock())), true);
        int crafting_time = portable ? 500 : 250;
        if (hunterPlayer.getSkillHandler().isSkillEnabled(HunterSkills.blood_potion_faster_crafting)) {
            crafting_time /= 2;
        }
        this.max_crafting_time = crafting_time;
        this.addPlayerSlots(playerInventory);
    }

    /**
     * @return If requirements met and not currently crafting
     */
    public boolean canCurrentlyStartCrafting() {
        return craftingTimer <= 0 && areRequirementsMet();

    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return portable || isWithinUsableDistance(this.worldPos, playerIn, ModBlocks.blood_potion_table);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener icontainerlistener : this.listeners) {
            if (this.prevCraftingTimer != this.craftingTimer) {
                icontainerlistener.sendWindowProperty(this, 0, craftingTimer);
            }

        }
        this.prevCraftingTimer = craftingTimer;
    }

    public IWorldPosCallable getWorldPosCallable() {
        return worldPos;
    }

    /**
     * @return The current crafting progress in 0..1
     */
    public float getCraftingPercentage() {
        return craftingTimer == 0 ? 0 : (1F - craftingTimer / (float) max_crafting_time);
    }

    @OnlyIn(Dist.CLIENT)
    public
    @Nullable
    List<String> getLocalizedCraftingHint() {
        ItemStack extra = itemHandler.getStackInSlot(3);
        if (extra.isEmpty()) return null;
        if (!hunterPlayer.getSkillHandler().isSkillEnabled(HunterSkills.blood_potion_category_hint)) return null;
        List<String> hints = BloodPotions.getLocalizedCategoryHint(extra);
        if (hints.isEmpty()) {
            hints.add(UtilLib.translate("text.vampirism.blood_potion.any_effect"));
        } else {
            hints.add(0, UtilLib.translate("text.vampirism.blood_potion.might_cause"));
        }
        return hints;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            clearContainer(playerIn);
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
            } else if (craftingTimer % 5 == 0) {
                //Abort crafting if requirements are not met anymore
                if (!areRequirementsMet()) {
                    craftingTimer = 0;
                }
            }

        }
    }

    @Nonnull
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index >= 4) {
                if (index < 31) {
                    if (!this.mergeItemStack(itemstack1, 0, 4, false)) {
                        return ItemStack.EMPTY;
                    } else if (!this.mergeItemStack(itemstack1, 31, 40, true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.mergeItemStack(itemstack1, 0, 31, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(itemstack1, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack.getCount() == itemstack1.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, itemstack1);
        }

        return itemstack;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0 && data >= 0 && data <= max_crafting_time) {
            craftingTimer = data;
        }
    }

    /**
     * @return if all required tileInventory are in the container
     */
    private boolean areRequirementsMet() {
        ItemStack garlic = itemHandler.getStackInSlot(2);
        if (garlic.isEmpty() || !ModItems.item_garlic.equals(garlic.getItem())) return false;
        boolean bottle = false;
        ItemStack bottle1 = itemHandler.getStackInSlot(0);
        ItemStack bottle2 = itemHandler.getStackInSlot(1);
        if (!bottle1.isEmpty() && bottle1.getItem().equals(ModItems.vampire_blood_bottle)) bottle = true;
        if (!bottle2.isEmpty() && bottle2.getItem().equals(ModItems.vampire_blood_bottle)) bottle = true;
        return bottle;
    }

    /**
     * Execute the crafting as long as the requirements are still met
     */
    private void onCraftingTimerFinished() {
        if (!areRequirementsMet()) return;
        ItemStack extraItem = itemHandler.extractItem(3, 1, false);
        itemHandler.extractItem(2, 1, false);
        ItemStack bottle1 = itemHandler.getStackInSlot(0);
        ItemStack bottle2 = itemHandler.getStackInSlot(1);
        if (!bottle1.isEmpty() && bottle1.getItem().equals(ModItems.vampire_blood_bottle)) {
            bottle1 = new ItemStack(ModItems.blood_potion);
            BloodPotions.chooseAndAddEffects(bottle1, hunterPlayer, extraItem);
        }
        if (!bottle2.isEmpty() && bottle2.getItem().equals(ModItems.vampire_blood_bottle)) {
            bottle2 = new ItemStack(ModItems.blood_potion);
            BloodPotions.chooseAndAddEffects(bottle2, hunterPlayer, extraItem);
        }
        itemHandler.setStackInSlot(0, bottle1);
        itemHandler.setStackInSlot(1, bottle2);
        hunterPlayer.getRepresentingPlayer().addStat(ModStats.blood_table);
    }
}
