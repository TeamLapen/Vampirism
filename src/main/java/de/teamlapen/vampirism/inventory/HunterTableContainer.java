package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.ItemPureBlood;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Container for the hunter table.
 * Handles inventory setup  and "crafting"
 */
public class HunterTableContainer extends InventoryContainer {

    private final HunterTableInventory inventory;
    private final SlotResult slotResult;
    private final int hunterLevel;
    private final HunterLevelingConf levelingConf = HunterLevelingConf.instance();
    private final BlockPos pos;
    private ItemStack missing = ItemStack.EMPTY;

    public HunterTableContainer(EntityPlayer player, BlockPos pos) {
        super(player.inventory, new HunterTableInventory(new InventorySlot.IItemSelector[]{((stack) -> Items.BOOK.equals(stack.getItem())), ((stack) -> ModItems.vampire_fang.equals(stack.getItem())), ((stack) -> stack.getItem() instanceof ItemPureBlood), (stack) -> ModItems.vampire_book.equals(stack.getItem())}));
        this.inventory = (HunterTableInventory) tile;
        inventory.setChangeListener(this);
        this.pos = pos;
        slotResult = new SlotResult(this, new InventoryCraftResult() {
            @Override
            public int getInventoryStackLimit() {
                return 1;
            }
        }, 0, 146, 28);
        this.addSlot(slotResult);
        hunterLevel = FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION);
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public HunterTableInventory getHunterInventory() {
        return inventory;
    }

    public ItemStack getMissingItems() {
        return missing;
    }

    public boolean isLevelValid() {
        return levelingConf.isLevelValidForTable(hunterLevel + 1);
    }

    public void onContainerClosed(EntityPlayer playerIn) {
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
        return InventoryHelper.checkItems(inventory, new Item[]{Items.BOOK, ModItems.vampire_fang, ItemPureBlood.getBloodItemForLevel(bloodLevel), ModItems.vampire_book}, new int[]{1, fangs, blood, par3});
    }

    private class SlotResult extends net.minecraft.inventory.Slot {

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
        public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
            container.onPickupResult();
            return stack;
        }
    }
}
