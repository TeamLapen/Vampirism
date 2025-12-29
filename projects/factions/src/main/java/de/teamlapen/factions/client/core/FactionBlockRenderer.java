package de.teamlapen.factions.client.core;

import de.teamlapen.factions.client.renderer.TotemRenderer;
import de.teamlapen.factions.common.core.FactionBlockEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class FactionBlockRenderer {

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FactionBlockEntities.TOTEM.get(), TotemRenderer::new);
    }
}
