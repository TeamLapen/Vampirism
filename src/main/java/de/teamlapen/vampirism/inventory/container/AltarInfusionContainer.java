package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.inventory.SimpleInventory;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.PureBloodItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.NonNullList;

public class AltarInfusionContainer extends InventoryContainer {

    public AltarInfusionContainer(int id, PlayerInventory playerInventory) {
        super(id, playerInventory, ModContainer.altar_infusion, new AltarInfusionInventory());
    }

    public AltarInfusionContainer(int id, PlayerInventory playerInventory, InventorySlot.IInventorySlotInventory inventory) {
        super(id, playerInventory, ModContainer.altar_infusion, inventory);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPos, playerIn, ModBlocks.altar_infusion);
    }

    public static class AltarInfusionInventory extends SimpleInventory {
        public AltarInfusionInventory() {
            super(NonNullList.from(new InventorySlot(PureBloodItem.class, 44, 34), new InventorySlot(ModItems.human_heart, 80, 34), new InventorySlot(ModItems.vampire_book, 116, 34)));

        }
    }
}
