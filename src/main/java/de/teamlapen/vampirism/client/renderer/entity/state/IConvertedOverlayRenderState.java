package de.teamlapen.vampirism.client.renderer.entity.state;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface IConvertedOverlayRenderState {

    @Nullable
    ResourceLocation vampirism$overlay();

    void vampirism$overlay(@Nullable ResourceLocation overlay);

    @Nullable
    ResourceLocation vampirism$convertedOverlay();

    void vampirism$convertedOverlay(@Nullable ResourceLocation overlay);
}
