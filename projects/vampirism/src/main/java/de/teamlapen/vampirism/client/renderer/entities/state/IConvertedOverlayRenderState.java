package de.teamlapen.vampirism.client.renderer.entities.state;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public interface IConvertedOverlayRenderState {

    @Nullable
    Identifier vampirism$overlay();

    void vampirism$overlay(@Nullable Identifier overlay);

    @Nullable
    Identifier vampirism$convertedOverlay();

    void vampirism$convertedOverlay(@Nullable Identifier overlay);
}
