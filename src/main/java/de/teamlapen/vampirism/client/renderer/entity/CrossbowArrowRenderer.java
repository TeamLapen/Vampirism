package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CrossbowArrowRenderer extends ArrowRenderer<CrossbowArrowEntity> {

    private static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public CrossbowArrowRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull CrossbowArrowEntity entity) {
        return RES_ARROW;
    }
}
