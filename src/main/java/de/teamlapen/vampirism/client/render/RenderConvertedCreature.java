package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Max on 13.02.2016.
 */
public class RenderConvertedCreature extends Render<EntityConvertedCreature> {
    public static boolean renderOverlay=false;
    public  RenderConvertedCreature(RenderManager renderManager) {
        super(renderManager);
    }


    @Override
    public void doRender(EntityConvertedCreature entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityCreature creature=entity.getOldCreature();
        if(creature!=null){
            creature.isDead=false;
            renderOverlay=true;
            this.renderManager.doRenderEntity(creature,x,y,z,entityYaw,partialTicks,false);
            renderOverlay=false;
            creature.isDead=true;
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityConvertedCreature entity) {
        return null;
    }
}
