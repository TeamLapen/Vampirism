package de.teamlapen.faction.misc.extensions.client;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface IAbstractContainerScreen {
    boolean invokeIsHovering(Slot slot, double mouseX, double mouseY);

    ItemStack getDraggingItem();
}
