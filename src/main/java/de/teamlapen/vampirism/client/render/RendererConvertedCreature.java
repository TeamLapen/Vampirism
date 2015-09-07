package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.entity.convertible.EntityConvertedCreature;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Max on 14.08.2015.
 */
public class RendererConvertedCreature extends Render {
    protected RendererConvertedCreature(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        this.doRender((EntityConvertedCreature) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }


    public void doRender(EntityConvertedCreature entity, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        Entity e = entity.getEntityCreature();

        if (e != null) {
            e.isDead = false;
            this.renderManager.doRenderEntity(entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_,false);
            e.isDead = true;
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return null;
    }
}
