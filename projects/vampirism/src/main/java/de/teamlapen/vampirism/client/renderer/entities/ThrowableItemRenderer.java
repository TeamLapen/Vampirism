package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.common.entity.ThrowableItemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * Renders the vampirism throwable entity item
 */
public class ThrowableItemRenderer extends ThrownItemRenderer<ThrowableItemEntity> {
    public ThrowableItemRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }
}
