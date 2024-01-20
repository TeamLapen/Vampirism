package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    @Invoker("isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z")
    boolean invoke_isHovering(Slot pSlot, double pMouseX, double pMouseY);

    @Accessor("draggingItem")
    ItemStack getDraggingItem();

}
