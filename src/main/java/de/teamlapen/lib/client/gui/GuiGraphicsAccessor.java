package de.teamlapen.lib.client.gui;

import net.minecraft.client.renderer.MultiBufferSource;

/**
 * injected into {@link net.minecraft.client.gui.GuiGraphics} in {@link de.teamlapen.vampirism.common.mixin.client.accessor.GuiGraphicsAccessor}
 */
public interface GuiGraphicsAccessor {

    MultiBufferSource.BufferSource getBufferSource();
}
