package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.hunter.HunterTaskMasterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class HunterTaskMasterRenderer extends BipedRenderer<HunterTaskMasterEntity, BipedModel<HunterTaskMasterEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png");

    public HunterTaskMasterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BipedModel<>(0F, 0F, 64, 64), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull HunterTaskMasterEntity entity) {
        return texture;
    }

    @Override
    protected void renderLivingLabel(HunterTaskMasterEntity entityIn, @Nonnull String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 4);
    }


}
