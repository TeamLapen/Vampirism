package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {

    @Accessor("inputSlots")
    Container getInputSlots();

    @Accessor("inputSlots")
    @Mutable
    void setInputSlots(Container inputSlots);
}
