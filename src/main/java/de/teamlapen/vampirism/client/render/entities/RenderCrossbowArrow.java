package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.EntityCrossbowArrow;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;


public class RenderCrossbowArrow extends RenderArrow<EntityCrossbowArrow> {

    private static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public RenderCrossbowArrow(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCrossbowArrow entity) {
        return RES_ARROW;
    }
}
