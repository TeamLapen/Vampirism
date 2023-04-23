package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainerMenu;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.HunterTableBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterLeveling;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.items.PureBloodItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Container for the hunter table.
 * Handles inventory setup  and "crafting"
 */
public class HunterTableMenu extends InventoryContainerMenu implements ContainerListener {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Items.BOOK, 15, 28), new SelectorInfo(ModItems.VAMPIRE_FANG.get(), 42, 28), new SelectorInfo(ModTags.Items.PURE_BLOOD, 69, 28), new SelectorInfo(ModItems.VAMPIRE_BOOK.get(), 96, 28)};
    private final @NotNull SlotResult slotResult;
    private final int hunterLevel;
    private final Optional<HunterLeveling.HunterTrainerRequirement> levelingReq;
    private ItemStack missing = ItemStack.EMPTY;


    public HunterTableMenu(int id, @NotNull Inventory playerInventory, ContainerLevelAccess worldPosCallable) {
        super(ModContainer.HUNTER_TABLE.get(), id, playerInventory, worldPosCallable, new SimpleContainer(SELECTOR_INFOS.length), SELECTOR_INFOS);
        ((SimpleContainer) inventory).addListener(this);
        slotResult = new SlotResult(this, new ResultContainer() {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        }, 4, 146, 28);
        this.addSlot(slotResult);
        hunterLevel = FactionPlayerHandler.get(playerInventory.player).getCurrentLevel(VReference.HUNTER_FACTION);
        this.addPlayerSlots(playerInventory);
        levelingReq = HunterLeveling.getTrainerRequirement(hunterLevel + 1);
    }

    @Override
    public void containerChanged(@NotNull Container invBasic) {
        slotsChanged(invBasic);
    }

    public ItemStack getMissingItems() {
        return missing;
    }

    public boolean isLevelValid(boolean considerTier) {
        return levelingReq.map(HunterLeveling.HunterTrainerRequirement::tableRequirement).map(level -> !considerTier || level.requiredTableTier() >= worldPos.evaluate(((world, blockPos) -> {
            BlockState state = world.getBlockState(blockPos);
            return state.hasProperty(HunterTableBlock.VARIANT) ? state.getValue(HunterTableBlock.VARIANT).tier : 0;
        })).orElse(0)).orElse(false);
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        if (!playerIn.getCommandSenderWorld().isClientSide) {
            clearContainer(playerIn, inventory);
        }
    }

    @Override
    public void slotsChanged(@NotNull Container inventoryIn) {
        if (isLevelValid(true)) {
            levelingReq.map(HunterLeveling.HunterTrainerRequirement::tableRequirement).ifPresent(x -> {
                checkItems(x.fangs(), x.blood(), x.blood_meta(), x.vampireBook());
            });
            if (missing.isEmpty()) {
                slotResult.container.setItem(0, new ItemStack(HunterIntelItem.getIntelForExactlyLevel(hunterLevel + 1)));
            } else {
                slotResult.container.setItem(0, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(worldPos, playerIn, ModBlocks.HUNTER_TABLE.get());
    }


    /**
     * Called when the resulting item is picked up
     */
    protected void onPickupResult() {
        this.levelingReq.map(HunterLeveling.HunterTrainerRequirement::tableRequirement).ifPresent(req -> {
            InventoryHelper.removeItems(inventory, req.fangs(), req.blood(), req.blood_meta(), req.vampireBook());
        });
    }

    /**
     * Checks if the given tileInventory are present
     */
    private ItemStack checkItems(int fangs, int blood, int bloodLevel, int par3) {
        return InventoryHelper.checkItems(inventory, new Item[]{Items.BOOK, ModItems.VAMPIRE_FANG.get(), PureBloodItem.getBloodItemForLevel(bloodLevel), ModItems.VAMPIRE_BOOK.get()}, new int[]{1, fangs, blood, par3});
    }

    public static class Factory implements IContainerFactory<HunterTableMenu> {

        @Override
        public @NotNull HunterTableMenu create(int windowId, @NotNull Inventory inv, @NotNull FriendlyByteBuf data) {
            BlockPos pos = data.readBlockPos();
            return new HunterTableMenu(windowId, inv, ContainerLevelAccess.create(inv.player.level, pos));
        }
    }

    private static class SlotResult extends Slot {

        private final HunterTableMenu hunterTableMenu;

        public SlotResult(HunterTableMenu container, @NotNull Container inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
            this.hunterTableMenu = container;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(@NotNull Player playerIn, @NotNull ItemStack stack) {
            hunterTableMenu.onPickupResult();
        }
    }
}
