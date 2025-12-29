package de.teamlapen.factions.misc.mixin.client.accessor;

import de.teamlapen.factions.misc.extensions.client.IAbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor extends IAbstractContainerScreen {

    @Override
    @Invoker("isHovering")
    boolean invokeIsHovering(Slot slot, double mouseX, double mouseY);

    @Override
    @Accessor("draggingItem")
    ItemStack getDraggingItem();
}
