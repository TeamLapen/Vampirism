package de.teamlapen.vampirism.common.world.items.recipes;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

public class ItemStackMatch {

    static ItemAccess forRecipeStack(ItemStack itemStack) {
        var container = VanillaContainerWrapper.of(new SimpleContainer(itemStack.copy()) {
            // Override to avoid clamping oversized stacks to their max stack size, just in case.
            @Override
            public void setItem(int slot, ItemStack stack, boolean performSideEffects) {
                getItems().set(slot, stack);
            }
        });
        return ItemAccess.forHandlerIndex(container, 0);
    }
}
