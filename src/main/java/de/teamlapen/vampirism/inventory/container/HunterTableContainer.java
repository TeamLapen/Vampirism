package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.HunterTableBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;

/**
 * Container for the hunter table.
 * Handles inventory setup  and "crafting"
 */
public class HunterTableContainer extends InventoryContainer implements IInventoryChangedListener {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Items.BOOK, 15, 28), new SelectorInfo(ModItems.VAMPIRE_FANG.get(), 42, 28), new SelectorInfo(ModTags.Items.PURE_BLOOD, 69, 28), new SelectorInfo(ModItems.VAMPIRE_BOOK.get(), 96, 28)};
    private final SlotResult slotResult;
    private final int hunterLevel;
    private final HunterLevelingConf levelingConf = HunterLevelingConf.instance();
    private ItemStack missing = ItemStack.EMPTY;


    public HunterTableContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(ModContainer.HUNTER_TABLE.get(), id, playerInventory, worldPosCallable, new Inventory(SELECTOR_INFOS.length), SELECTOR_INFOS);
        ((Inventory) inventory).addListener(this);
        slotResult = new SlotResult(this, new CraftResultInventory() {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        }, 4, 146, 28);
        this.addSlot(slotResult);
        hunterLevel = FactionPlayerHandler.get(playerInventory.player).getCurrentLevel(VReference.HUNTER_FACTION);
        this.addPlayerSlots(playerInventory);
    }

    @Override
    public void containerChanged(IInventory invBasic) {
        slotsChanged(invBasic);
    }

    public ItemStack getMissingItems() {
        return missing;
    }

    public boolean isLevelValid(boolean considerTier) {
        return considerTier ? levelingConf.isLevelValidForTableTier(hunterLevel + 1, worldPos.evaluate(((world, blockPos) -> {
            BlockState state = world.getBlockState(blockPos);
            return state.hasProperty(HunterTableBlock.VARIANT) ? state.getValue(HunterTableBlock.VARIANT).tier : 0;
        })).orElse(0)) : levelingConf.isLevelValidForTable(hunterLevel + 1);
    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        if (!playerIn.getCommandSenderWorld().isClientSide) {
            clearContainer(playerIn, playerIn.level, inventory);
        }
    }

    @Override
    public void slotsChanged(IInventory inventoryIn) {
        if (isLevelValid(true)) {
            int[] req = levelingConf.getItemRequirementsForTable(hunterLevel + 1);
            missing = checkItems(req[0], req[1], req[2], req[3]);
            if (missing.isEmpty()) {
                slotResult.container.setItem(0, new ItemStack(HunterIntelItem.getIntelForExactlyLevel(hunterLevel + 1)));
            } else {
                slotResult.container.setItem(0, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return stillValid(worldPos, playerIn, ModBlocks.HUNTER_TABLE.get());
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
        return InventoryHelper.checkItems(inventory, new Item[]{Items.BOOK, ModItems.VAMPIRE_FANG.get(), PureBloodItem.getBloodItemForLevel(bloodLevel), ModItems.VAMPIRE_BOOK.get()}, new int[]{1, fangs, blood, par3});
    }

    public static class Factory implements IContainerFactory<HunterTableContainer> {

        @Override
        public HunterTableContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            BlockPos pos = data.readBlockPos();
            return new HunterTableContainer(windowId, inv, IWorldPosCallable.create(inv.player.level, pos));
        }
    }

    private static class SlotResult extends Slot {

        private final HunterTableContainer hunterTableContainer;

        public SlotResult(HunterTableContainer container, IInventory inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
            this.hunterTableContainer = container;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
            hunterTableContainer.onPickupResult();
            return stack;
        }
    }
}
