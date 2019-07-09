package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.EntityCrossbowArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCrossbowArrow extends ArrowRenderer<EntityCrossbowArrow> {

    private static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public RenderCrossbowArrow(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCrossbowArrow entity) {
        return RES_ARROW;
    }
}
