package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.common.world.entity.ThrowableItemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/**
 * Renders the vampirism throwable entity item
 */
public class ThrowableItemRenderer extends ThrownItemRenderer<ThrowableItemEntity> {
    public ThrowableItemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
