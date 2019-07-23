package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;

/**
 * Container which handles hunter levelup at an hunter trainer
 */
public class HunterTrainerContainer extends InventoryContainer {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Ingredient.fromItems(Items.IRON_INGOT), 27, 26), new SelectorInfo(Ingredient.fromItems(Items.GOLD_INGOT), 57, 26), new SelectorInfo(Ingredient.fromTag(ModTags.Items.HUNTER_INTEL), 86, 26)};
    private final PlayerEntity player;
    private boolean changed = false;
    private ItemStack missing = ItemStack.EMPTY;

    @Deprecated
    public HunterTrainerContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public HunterTrainerContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosIn) {
        super(ModContainer.hunter_trainer, id, playerInventory, worldPosIn, SELECTOR_INFOS);
        this.player = playerInventory.player;
        this.addPlayerSlots(playerInventory);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    /**
     * @return If the player can levelup with the given tileInventory
     */
    public boolean canLevelup() {
        int targetLevel = FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION) + 1;
        HunterLevelingConf levelingConf = HunterLevelingConf.instance();
        if (!levelingConf.isLevelValidForTrainer(targetLevel)) return false;
        int[] req = levelingConf.getItemRequirementsForTrainer(targetLevel);
        int level = levelingConf.getHunterIntelMetaForLevel(targetLevel);
        missing = InventoryHelper.checkItems(NonNullList.from(ItemStack.EMPTY, this.inventoryItemStacks.get(0), this.inventoryItemStacks.get(1), this.inventoryItemStacks.get(2)), new Item[]{Items.IRON_INGOT, Items.GOLD_INGOT, HunterIntelItem.getIntelForLevel(level)}, new int[]{req[0], req[1], 1});
        return missing.isEmpty();
    }

    /**
     * @return The missing Itemstack or null if nothing is missing
     */
    public ItemStack getMissingItems() {
        return this.missing;
    }

    /**
     * @return If the inventory has changed since the last call
     */
    public boolean hasChanged() {
        if (changed) {
            changed = false;
            return true;
        }
        return false;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            for (int i = 0; i < 3; ++i) {
                ItemStack itemstack = this.inventoryItemStacks.remove(i);

                if (!itemstack.isEmpty()) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }

    /**
     * Called via input packet, when the player clicks the levelup button.
     */
    public void onLevelupClicked() {
        if (canLevelup()) {
            int old = FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION);
            FactionPlayerHandler.get(player).setFactionLevel(VReference.HUNTER_FACTION, old + 1);
            int[] req = HunterLevelingConf.instance().getItemRequirementsForTrainer(old + 1);
            InventoryHelper.removeItems(inventoryItemStacks, new int[]{req[0], req[1], 1});
            player.addPotionEffect(new EffectInstance(ModEffects.saturation, 400, 2));
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index >= 3) {
                if (index < 30) {
                    if (!this.mergeItemStack(slotStack, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    } else if (this.mergeItemStack(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.mergeItemStack(slotStack, 0, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(slotStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }

        return result;
    }
}
