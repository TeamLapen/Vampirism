package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModMenus;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireLeveling;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AltarInfusionMenu extends ItemCombinerMenu {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<VampireLeveling.AltarInfusionRequirements> lvlRequirement;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public AltarInfusionMenu(int id, @NotNull Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(3), ContainerLevelAccess.NULL);
    }

    public AltarInfusionMenu(int id, @NotNull Inventory playerInventory, @NotNull Container inventory, ContainerLevelAccess worldPosCallable) {
        super(ModMenus.ALTAR_INFUSION.get(), id, playerInventory, worldPosCallable, createInputSlotDefinition());
        this.setInputSlots(inventory);
        for (int i = 0; i < 3; i++) {
            this.slots.get(i).setContainer(this.getInputSlots());
        }
        this.lvlRequirement = VampireLeveling.getInfusionRequirement(FactionPlayerHandler.get(player).getCurrentLevel(ModFactions.VAMPIRE) + 1);
    }

    public Optional<VampireLeveling.AltarInfusionRequirements> getRequirement() {
        return lvlRequirement;
    }


    @Override
    protected boolean mayPickup(@NotNull Player pPlayer, boolean pHasStack) {
        return true;
    }

    @Override
    protected void onTake(@NotNull Player player, @NotNull ItemStack stack) {

    }

    @Override
    protected void clearContainer(@NotNull Player pPlayer, @NotNull Container pContainer) {
    }

    @Override
    protected boolean isValidBlock(BlockState pState) {
        return pState.is(ModBlocks.ALTAR_INFUSION.get());
    }

    @Override
    public void createResult() {

    }

    @Override
    public void createResultSlot(@NotNull ItemCombinerMenuSlotDefinition definition) {

    }

    public static ItemCombinerMenuSlotDefinition createInputSlotDefinition() {
        return ModifiedItemCombinerMenuSlotDefinition.createWithoutResult()
                .withSlot(0, 44, 34, stack -> stack.is(ModItemTags.PURE_BLOOD))
                .withSlot(1, 80, 34, stack -> stack.is(ModItems.HUMAN_HEART.get()))
                .withSlot(2, 116, 34, stack -> stack.is(ModItems.VAMPIRE_BOOK.get()))
                .build();
    }
}
