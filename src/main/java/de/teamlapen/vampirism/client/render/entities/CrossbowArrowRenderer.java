package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrossbowArrowRenderer extends ArrowRenderer<CrossbowArrowEntity> {

    private static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public CrossbowArrowRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(CrossbowArrowEntity entity) {
        return RES_ARROW;
    }
}
