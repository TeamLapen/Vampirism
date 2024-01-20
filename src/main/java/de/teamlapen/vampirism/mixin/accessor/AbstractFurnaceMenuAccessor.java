package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.transformer.meta.MixinInner;

@Mixin(AbstractFurnaceMenu.class)
public interface AbstractFurnaceMenuAccessor {

    @Accessor("container")
    Container getContainer();
}
