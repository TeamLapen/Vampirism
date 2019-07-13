package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.inventory.SimpleInventory;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Container for the hunter table.
 * Handles inventory setup  and "crafting"
 */
public class HunterTableContainer extends InventoryContainer {

    private final SlotResult slotResult;
    private final int hunterLevel;
    private final HunterLevelingConf levelingConf = HunterLevelingConf.instance();
    private ItemStack missing = ItemStack.EMPTY;

    public HunterTableContainer(int id, PlayerInventory playerInventory) {
        super(id, playerInventory, ModContainer.hunter_table, new HunterTableInventory());
        ((SimpleInventory) inventory).setChangeListener(this);
        slotResult = new SlotResult(this, new CraftResultInventory() {
            @Override
            public int getInventoryStackLimit() {
                return 1;
            }
        }, 0, 146, 28);
        this.addSlot(slotResult);
        hunterLevel = FactionPlayerHandler.get(playerInventory.player).getCurrentLevel(VReference.HUNTER_FACTION);
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
            for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.inventory.removeStackFromSlot(i);

                if (!itemstack.isEmpty()) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }

    @Override
    public void onInventoryChanged() {
        if (inventory != null && isLevelValid()) {
            int[] req = levelingConf.getItemRequirementsForTable(hunterLevel + 1);
            missing = checkItems(req[0], req[1], req[2], req[3]);
            if (missing.isEmpty()) {
                slotResult.inventory.setInventorySlotContents(0, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(REFERENCE.MODID, "hunter_intel" + levelingConf.getHunterIntelMetaForLevel(hunterLevel + 1))), 1));
            } else {
                slotResult.inventory.setInventorySlotContents(0, ItemStack.EMPTY);
            }
        }
    }

    /**
     * Called when the resulting item is picked up
     */
    protected void onPickupResult() {
        int[] req = levelingConf.getItemRequirementsForTable(hunterLevel + 1);
        InventoryHelper.removeItems(inventory, new int[]{1, req[0], req[1], req[3]});
        onInventoryChanged();
    }

    /**
     * Checks if the given items are present

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

    protected static class HunterTableInventory extends SimpleInventory {
        protected HunterTableInventory() {
            super(NonNullList.from(new InventorySlot(Items.BOOK, 15, 28), new InventorySlot(ModItems.vampire_fang, 42, 28), new InventorySlot(PureBloodItem.class, 69, 28), new InventorySlot(ModItems.vampire_book, 96, 28)));

        }
    }

}
