package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RevertBackMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess pAccess;

    public RevertBackMenu(int pContainerId, Inventory container) {
        this(pContainerId, container, ContainerLevelAccess.NULL);
    }

    public RevertBackMenu(int pContainerId, Inventory container, ContainerLevelAccess pAccess) {
        super(ModMenus.REVERT_BACK.get(), pContainerId);
        this.pAccess = pAccess;
        this.addSlot(new Slot(container, container.selected, 80, 35));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int pSlotId, int pButton, @NotNull ClickType pClickType, @NotNull Player pPlayer) {
        super.clicked(pSlotId, pButton, pClickType, pPlayer);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.pAccess, player, ModBlocks.MED_CHAIR.get());
    }

    public void consume() {
        Slot slot = this.getSlot(0);
        slot.getItem().shrink(1);
    }
}
