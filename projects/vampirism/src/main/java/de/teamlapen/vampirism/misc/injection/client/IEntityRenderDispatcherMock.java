package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

public interface IEntityRenderDispatcherMock extends IEntityRenderDispatcher {
    @Override
    default Map<EntityType<?>, EntityRenderer<?, ?>> getRenderers() {
        return Map.of();
    }
}
