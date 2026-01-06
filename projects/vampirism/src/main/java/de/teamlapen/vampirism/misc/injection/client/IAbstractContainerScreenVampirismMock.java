package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.faction.misc.extensions.client.IAbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Deprecated
public interface IAbstractContainerScreenVampirismMock extends IAbstractContainerScreen {
    @Override
    default boolean invokeIsHovering(Slot slot, double mouseX, double mouseY) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default ItemStack getDraggingItem() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
