package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IItemCombinerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor extends IItemCombinerMenu {

    @Override
    @Accessor("inputSlots")
    Container getInputSlots();
}
