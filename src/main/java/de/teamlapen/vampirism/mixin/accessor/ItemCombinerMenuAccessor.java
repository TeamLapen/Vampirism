package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {

    @Accessor("inputSlots")
    Container getInputSlots();

    @Accessor("inputSlots")
    void setInputSlots(Container inputSlots);
}
