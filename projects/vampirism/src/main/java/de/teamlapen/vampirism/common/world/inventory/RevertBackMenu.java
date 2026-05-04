package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModMenus;
import de.teamlapen.vampirism.common.world.items.InjectionItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RevertBackMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess pAccess;

    public RevertBackMenu(int pContainerId, Inventory container) {
        this(pContainerId, container, ContainerLevelAccess.NULL);
    }

    public RevertBackMenu(int pContainerId, Inventory container, ContainerLevelAccess pAccess) {
        super(ModMenus.REVERT_BACK.get(), pContainerId);
        this.pAccess = pAccess;
        this.addSlot(new Slot(container, container.getSelectedSlot(), 80, 35));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.pAccess, player, ModBlocks.INJECTION_CHAIR.get());
    }

    public void consume(Player player) {
        Slot slot = this.getSlot(0);
        if (slot.getItem().getItem() instanceof InjectionItem injectionItem) {
            injectionItem.consumeInjectionItem(slot.getItem(), player, player.getUsedItemHand());
        }
    }
}
