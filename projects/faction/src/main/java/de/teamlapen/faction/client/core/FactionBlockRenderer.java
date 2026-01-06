package de.teamlapen.faction.client.core;

import de.teamlapen.faction.client.renderer.TotemRenderer;
import de.teamlapen.faction.common.core.FactionBlockEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class FactionBlockRenderer {

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FactionBlockEntities.TOTEM.get(), TotemRenderer::new);
    }
}
