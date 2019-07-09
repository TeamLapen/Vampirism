package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrossbowArrowRenderer extends ArrowRenderer<CrossbowArrowEntity> {

    private static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public CrossbowArrowRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(CrossbowArrowEntity entity) {
        return RES_ARROW;
    }
}
