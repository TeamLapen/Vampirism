package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.potion.blood.BloodPotions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Table to create blood potions
 */
public class BloodPotionTableContainer extends InventoryContainer {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(ModItems.vampire_blood_bottle, 115, 55, false, 1, null), new SelectorInfo(ModItems.vampire_blood_bottle, 137, 55, false, 1, null), new SelectorInfo(ModTags.Items.GARLIC, 126, 14), new SelectorInfo(getSpecialIngredient(ModTags.Items.GARLIC, ModItems.vampire_blood_bottle), 101, 22, true, 64, null)};

    private static LazyOptional<Collection<Item>> getSpecialIngredient(ITag<Item> tag, Item... items) {
        return LazyOptional.of(() -> {
            Set<Item> set = new HashSet<>(tag.getAllElements());
            Collections.addAll(set, items);
            return set;
        });
    }

    private final HunterPlayer hunterPlayer;
    private final int max_crafting_time;
    private final boolean portable;
    private int craftingTimer = 0;
    private int prevCraftingTimer = 0;

    public BloodPotionTableContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosIn) {
        super(ModContainer.blood_potion_table, id, playerInventory, worldPosIn, new Inventory(SELECTOR_INFOS.length), SELECTOR_INFOS);
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

    /**
     * @return The current crafting progress in 0..1
     */
    public float getCraftingPercentage() {
        return craftingTimer == 0 ? 0 : (1F - craftingTimer / (float) max_crafting_time);
    }


    public IWorldPosCallable getWorldPosCallable() {
        return worldPos;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            clearContainer(playerIn, playerIn.world, inventory);
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
        ItemStack itemstackCopy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstackCopy = itemstack1.copy();
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

            if (itemstackCopy.getCount() == itemstack1.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, itemstack1);
        }

        return itemstackCopy;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0 && data >= 0 && data <= max_crafting_time) {
            craftingTimer = data;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public
    @Nullable
    List<ITextComponent> getLocalizedCraftingHint() {
        ItemStack extra = inventory.getStackInSlot(3);
        if (extra.isEmpty()) return null;
        if (!hunterPlayer.getSkillHandler().isSkillEnabled(HunterSkills.blood_potion_category_hint)) return null;
        List<ITextComponent> hints = BloodPotions.getLocalizedCategoryHint(extra);
        if (hints.isEmpty()) {
            hints.add(new TranslationTextComponent("text.vampirism.blood_potion.any_effect"));
        } else {
            hints.add(0, new TranslationTextComponent("text.vampirism.blood_potion.might_cause"));
        }
        return hints;
    }

    /**
     * @return if all required tileInventory are in the container
     */
    private boolean areRequirementsMet() {
        ItemStack garlic = inventory.getStackInSlot(2);
        if (garlic.isEmpty() || !ModTags.Items.GARLIC.contains(garlic.getItem())) return false;
        boolean bottle = false;
        ItemStack bottle1 = inventory.getStackInSlot(0);
        ItemStack bottle2 = inventory.getStackInSlot(1);
        if (!bottle1.isEmpty() && bottle1.getItem().equals(ModItems.vampire_blood_bottle)) bottle = true;
        if (!bottle2.isEmpty() && bottle2.getItem().equals(ModItems.vampire_blood_bottle)) bottle = true;
        return bottle;
    }

    /**
     * Execute the crafting as long as the requirements are still met
     */
    private void onCraftingTimerFinished() {
        if (!areRequirementsMet()) return;
        ItemStack extraItem = inventory.getStackInSlot(3);
        if (!extraItem.isEmpty()) {
            extraItem = extraItem.copy();
            extraItem.setCount(1);
            inventory.decrStackSize(3, 1);
        }
        inventory.decrStackSize(2, 1);
        ItemStack bottle1 = inventory.getStackInSlot(0);
        ItemStack bottle2 = inventory.getStackInSlot(1);
        if (!bottle1.isEmpty() && bottle1.getItem().equals(ModItems.vampire_blood_bottle)) {
            bottle1 = new ItemStack(ModItems.blood_potion);
            BloodPotions.chooseAndAddEffects(bottle1, hunterPlayer, extraItem);
        }
        if (!bottle2.isEmpty() && bottle2.getItem().equals(ModItems.vampire_blood_bottle)) {
            bottle2 = new ItemStack(ModItems.blood_potion);
            BloodPotions.chooseAndAddEffects(bottle2, hunterPlayer, extraItem);
        }
        inventory.setInventorySlotContents(0, bottle1);
        inventory.setInventorySlotContents(1, bottle2);
        hunterPlayer.getRepresentingPlayer().addStat(ModStats.blood_table);
    }

    public static class Factory implements IContainerFactory<BloodPotionTableContainer> {

        @Override
        public BloodPotionTableContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            BlockPos pos = data.readBlockPos();
            return new BloodPotionTableContainer(windowId, inv, IWorldPosCallable.of(inv.player.world, pos));
        }
    }
}
