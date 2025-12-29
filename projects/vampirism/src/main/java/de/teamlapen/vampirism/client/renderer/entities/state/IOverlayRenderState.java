package de.teamlapen.vampirism.client.renderer.entities.state;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public interface IOverlayRenderState {

    @Nullable
    Identifier overlay();
}
