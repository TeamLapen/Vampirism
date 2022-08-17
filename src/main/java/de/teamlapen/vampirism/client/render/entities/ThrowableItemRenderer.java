package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.ThrowableItemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Renders the vampirism throwable entity item
 */
@OnlyIn(Dist.CLIENT)
public class ThrowableItemRenderer extends ThrownItemRenderer<ThrowableItemEntity> {
    public ThrowableItemRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }
}
