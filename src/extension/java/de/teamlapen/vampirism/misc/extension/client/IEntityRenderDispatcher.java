package de.teamlapen.vampirism.misc.extension.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

public interface IEntityRenderDispatcher {
    Map<EntityType<?>, EntityRenderer<?, ?>> getRenderers();

}
