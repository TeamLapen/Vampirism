package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Renders a converted creature, by rendering it's old creature
 */
@OnlyIn(Dist.CLIENT)
public class RenderConvertedCreature extends Render<EntityConvertedCreature> {
    public static boolean renderOverlay = false;

    public RenderConvertedCreature(RenderManager renderManager) {
        super(renderManager);
    }


    @Override
    public void doRender(EntityConvertedCreature entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityCreature creature = entity.getOldCreature();
        if (creature != null) {
            creature.removed = false;
            renderOverlay = true;
            this.renderManager.renderEntity(creature, x, y, z, entityYaw, partialTicks, false);
            renderOverlay = false;
            creature.removed = true;
        }
    }

    @Override
    protected
    @Nullable
    ResourceLocation getEntityTexture(EntityConvertedCreature entity) {
        return null;
    }
}
