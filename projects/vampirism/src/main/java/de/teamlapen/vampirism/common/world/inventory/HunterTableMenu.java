package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModMenus;
import de.teamlapen.vampirism.common.world.blocks.HunterTableBlock;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterLeveling;
import de.teamlapen.vampirism.common.world.items.PureBloodItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.IntStream;

public class HunterTableMenu extends ItemCombinerMenu {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<HunterLeveling.HunterTableRequirement> tableRequirement;

    public HunterTableMenu(int id, @NotNull Inventory playerInventory, ContainerLevelAccess worldPosCallable) {
        super(ModMenus.HUNTER_TABLE.get(), id, playerInventory, worldPosCallable, createInputSlotDefinitions(playerInventory.player));
        int hunterLevel = FactionPlayerHandler.get(playerInventory.player).getCurrentLevel(ModFactions.HUNTER);
        this.tableRequirement = HunterLeveling.getTrainerRequirement(hunterLevel + 1).map(HunterLeveling.HunterTrainerRequirement::tableRequirement);
    }

    public Optional<HunterLeveling.HunterTableRequirement> getTableRequirement() {
        return this.tableRequirement.filter(this::doesTableFulfillRequirement);
    }

    public Optional<HunterLeveling.HunterTableRequirement> getRequirement() {
        return this.tableRequirement;
    }

    protected static @NotNull ItemCombinerMenuSlotDefinition createInputSlotDefinitions(Player player) {
        int hunterLevel = FactionPlayerHandler.get(player).getCurrentLevel(ModFactions.HUNTER);
        var tableRequirement = HunterLeveling.getTrainerRequirement(hunterLevel + 1).map(HunterLeveling.HunterTrainerRequirement::tableRequirement);
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 15, 28, stack -> stack.is(Items.BOOK))
                .withSlot(1, 42, 28, stack -> stack.is(ModItems.VAMPIRE_FANG.get()))
                .withSlot(2, 69, 28, stack -> tableRequirement.filter(req -> req.pureBloodLevel() <= (stack.getItem() instanceof PureBloodItem pure ? pure.getLevel(stack) : -1)).isPresent())
                .withSlot(3, 96, 28, stack -> stack.is(ModItems.VAMPIRE_BOOK.get()))
                .withResultSlot(4, 146, 28)
                .build();
    }

    @Override
    protected boolean mayPickup(@NotNull Player pPlayer, boolean pHasStack) {
        return true;
    }

    @Override
    protected void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.tableRequirement.ifPresent(req -> {
            InventoryHelper.removeItems(getInputSlots(), req.bookQuantity(), req.vampireFangQuantity(), req.pureBloodQuantity(), req.vampireBookQuantity());
        });
    }

    @Override
    protected boolean isValidBlock(@NotNull BlockState pState) {
        return pState.is(ModBlocks.HUNTER_TABLE.get());
    }

    @Override
    public void createResult() {
        this.tableRequirement.filter(this::isRequirementFulfilled).map(s -> s.resultIntelItem().get()).ifPresentOrElse(intel -> {
            this.resultSlots.setItem(0, intel.getDefaultInstance());
        }, () -> {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        });
    }

    private boolean isRequirementFulfilled(HunterLeveling.HunterTableRequirement req) {
        return getInputSlots().countItem(Items.BOOK) >= 1
                && getInputSlots().countItem(ModItems.VAMPIRE_FANG.get()) >= req.vampireFangQuantity()
                && countPureBlood(req) >= req.pureBloodQuantity()
                && getInputSlots().countItem(ModItems.VAMPIRE_BOOK.get()) >= req.vampireBookQuantity();
    }

    private int countPureBlood(HunterLeveling.HunterTableRequirement req) {
        return IntStream.range(req.pureBloodLevel(), 5).mapToObj(PureBloodItem::getBloodItemForLevel).mapToInt(getInputSlots()::countItem).sum();
    }

    public boolean doesTableFulfillRequirement(HunterLeveling.HunterTableRequirement req) {
        return req.requiredTableTier() <= this.access.evaluate((level, pos) -> HunterTableBlock.getVariantValue(level.getBlockState(pos)), 0);
    }

    public static class Factory implements IContainerFactory<HunterTableMenu> {

        @Override
        public @NotNull HunterTableMenu create(int windowId, @NotNull Inventory inv, @NotNull RegistryFriendlyByteBuf data) {
            BlockPos pos = data.readBlockPos();
            return new HunterTableMenu(windowId, inv, ContainerLevelAccess.create(inv.player.level(), pos));
        }
    }
}
