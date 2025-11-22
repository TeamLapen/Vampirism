package de.teamlapen.factions.client.core;

import de.teamlapen.factions.client.renderer.TotemRenderer;
import de.teamlapen.factions.common.core.FactionBlockEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

public class ModBlockRenderer {

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FactionBlockEntities.TOTEM.get(), TotemRenderer::new);
    }
}
