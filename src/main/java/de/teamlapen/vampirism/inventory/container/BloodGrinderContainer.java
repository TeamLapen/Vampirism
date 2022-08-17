package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.core.ModContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BloodGrinderContainer extends InventoryContainer {
    private static final Predicate<ItemStack> canProcess = BloodConversionRegistry::canBeConverted;
    public static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(canProcess, 80, 34)};

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public BloodGrinderContainer(int id, @NotNull Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(1), ContainerLevelAccess.NULL);
    }

    public BloodGrinderContainer(int id, @NotNull Inventory playerInventory, @NotNull Container inventory, ContainerLevelAccess worldPosIn) {
        super(ModContainer.BLOOD_GRINDER.get(), id, playerInventory, worldPosIn, inventory, SELECTOR_INFOS);
        this.addPlayerSlots(playerInventory);
    }
}
