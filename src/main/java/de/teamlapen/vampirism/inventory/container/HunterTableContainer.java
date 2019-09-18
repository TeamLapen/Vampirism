package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IWorldPosCallable;

/**
 * Container for the hunter table.
 * Handles inventory setup  and "crafting"
 */
public class HunterTableContainer extends InventoryContainer {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Ingredient.fromItems(Items.BOOK), 15, 28), new SelectorInfo(Ingredient.fromItems(ModItems.vampire_fang), 42, 28), new SelectorInfo(Ingredient.fromTag(ModTags.Items.PURE_BLOOD), 69, 28), new SelectorInfo(Ingredient.fromItems(ModItems.vampire_book), 96, 28)};
    private final SlotResult slotResult;
    private final int hunterLevel;
    private final HunterLevelingConf levelingConf = HunterLevelingConf.instance();
    private ItemStack missing = ItemStack.EMPTY;

    @Deprecated
    public HunterTableContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public HunterTableContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(ModContainer.hunter_table, id, playerInventory, worldPosCallable, new Inventory(SELECTOR_INFOS.length), SELECTOR_INFOS);
        slotResult = new SlotResult(this, new CraftResultInventory() {
            @Override
            public int getInventoryStackLimit() {
                return 1;
            }
        }, 4, 146, 28);
        this.addSlot(slotResult);
        hunterLevel = FactionPlayerHandler.get(playerInventory.player).getCurrentLevel(VReference.HUNTER_FACTION);
        this.addPlayerSlots(playerInventory);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(worldPos, playerIn, ModBlocks.hunter_table);
    }

    public ItemStack getMissingItems() {
        return missing;
    }

    public boolean isLevelValid() {
        return levelingConf.isLevelValidForTable(hunterLevel + 1);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            clearContainer(playerIn, playerIn.world, inventory);
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (isLevelValid()) {
            int[] req = levelingConf.getItemRequirementsForTable(hunterLevel + 1);
            missing = checkItems(req[0], req[1], req[2], req[3]);
            if (missing.isEmpty()) {
                slotResult.inventory.setInventorySlotContents(0, new ItemStack(HunterIntelItem.getIntelForExactlyLevel(hunterLevel + 1)));
            } else {
                slotResult.inventory.setInventorySlotContents(0, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index == 4) {
                if (!this.mergeItemStack(slotStack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5) {
                if (index < 32) {
                    if (!this.mergeItemStack(slotStack, 0, 5, false)) {
                        return ItemStack.EMPTY;
                    } else if (this.mergeItemStack(slotStack, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.mergeItemStack(slotStack, 0, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(slotStack, 5, 41, false)) {
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
        onCraftMatrixChanged(null);
        return result;
    }

    /**
     * Called when the resulting item is picked up
     */
    protected void onPickupResult() {
        int[] req = levelingConf.getItemRequirementsForTable(hunterLevel + 1);
        InventoryHelper.removeItems(inventory, new int[]{1, req[0], req[1], req[3]});
    }

    /**
     * Checks if the given tileInventory are present
     */
    private ItemStack checkItems(int fangs, int blood, int bloodLevel, int par3) {
        return InventoryHelper.checkItems(inventory, new Item[]{Items.BOOK, ModItems.vampire_fang, PureBloodItem.getBloodItemForLevel(bloodLevel), ModItems.vampire_book}, new int[]{1, fangs, blood, par3});
    }

    private class SlotResult extends Slot {

        private final HunterTableContainer container;

        public SlotResult(HunterTableContainer container, IInventory inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
            this.container = container;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
            container.onPickupResult();
            return stack;
        }
    }
}
