package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.faction.common.world.inventory.InventoryContainerMenu;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.common.core.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class BloodGrinderMenu extends InventoryContainerMenu {
    private static final Predicate<ItemStack> canProcess = VampirismApi.services().bloodConversionRegistry()::canBeConverted;
    public static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[] {new SelectorInfo(canProcess, 80, 34)};

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public BloodGrinderMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(1), ContainerLevelAccess.NULL);
    }

    public BloodGrinderMenu(int id, Inventory playerInventory, Container inventory, ContainerLevelAccess worldPosIn) {
        super(ModMenus.BLOOD_GRINDER.get(), id, playerInventory, worldPosIn, inventory, SELECTOR_INFOS);
        this.addPlayerSlots(playerInventory);
    }

    public boolean hasItem() {
        ItemStack item = this.inventory.getItem(0);
        return !item.isEmpty();
    }
}
