package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IAbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface IAbstractContainerScreenMock extends IAbstractContainerScreen {
    @Override
    default boolean invokeIsHovering(Slot slot, double mouseX, double mouseY) {
        return false;
    }

    @Override
    default ItemStack getDraggingItem() {
        return null;
    }
}
