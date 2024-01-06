package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.entity.ThrowableItemEntity;
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
