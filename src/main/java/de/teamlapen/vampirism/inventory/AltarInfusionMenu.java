package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainerMenu;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class AltarInfusionMenu extends InventoryContainerMenu {
    public static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(ModTags.Items.PURE_BLOOD, 44, 34), new SelectorInfo(ModItems.HUMAN_HEART.get(), 80, 34), new SelectorInfo(ModItems.VAMPIRE_BOOK.get(), 116, 34)};

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public AltarInfusionMenu(int id, @NotNull Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(3), ContainerLevelAccess.NULL);
    }

    public AltarInfusionMenu(int id, @NotNull Inventory playerInventory, @NotNull Container inventory, ContainerLevelAccess worldPosCallable) {
        super(ModContainer.ALTAR_INFUSION.get(), id, playerInventory, worldPosCallable, inventory, SELECTOR_INFOS);
        this.addPlayerSlots(playerInventory);
    }


    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(this.worldPos, playerIn, ModBlocks.ALTAR_INFUSION.get());
    }
}
