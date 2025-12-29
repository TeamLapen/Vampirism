package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

@Deprecated
public interface IEntityRenderDispatcherVampirismMock extends IEntityRenderDispatcher {
    @Override
    default Map<EntityType<?>, EntityRenderer<?, ?>> getRenderers() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
