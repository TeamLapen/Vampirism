package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IWorldPosCallable;

public class AltarInfusionContainer extends InventoryContainer {
    public static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(ModTags.Items.PURE_BLOOD, 44, 34), new SelectorInfo(ModItems.HUMAN_HEART.get(), 80, 34), new SelectorInfo(ModItems.VAMPIRE_BOOK.get(), 116, 34)};

    @Deprecated
    public AltarInfusionContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(3), IWorldPosCallable.NULL);
        this.addPlayerSlots(playerInventory);
    }

    public AltarInfusionContainer(int id, PlayerInventory playerInventory, IInventory inventory, IWorldPosCallable worldPosCallable) {
        super(ModContainer.ALTAR_INFUSION.get(), id, playerInventory, worldPosCallable, inventory, SELECTOR_INFOS);
        this.addPlayerSlots(playerInventory);
    }


    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return stillValid(this.worldPos, playerIn, ModBlocks.ALTAR_INFUSION.get());
    }
}
