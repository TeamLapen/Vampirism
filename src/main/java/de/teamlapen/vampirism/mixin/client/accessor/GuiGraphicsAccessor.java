package de.teamlapen.vampirism.mixin.client.accessor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor extends de.teamlapen.lib.util.GuiGraphicsAccessor {

    @Accessor("bufferSource")
    @Override
    MultiBufferSource.BufferSource getBufferSource();
}
