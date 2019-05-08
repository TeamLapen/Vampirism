package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.render.LayerAdvancedVampireEye;
import de.teamlapen.vampirism.client.render.LayerPlayerFaceOverlay;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedVampireRenderer extends BipedRenderer<AdvancedVampireEntity, BipedModel<AdvancedVampireEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png");

    public AdvancedVampireRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BipedModel<>(0F, 0F, 64, 64), 0.5F);
        if (VampirismConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new LayerPlayerFaceOverlay<>(this));
            this.addLayer(new LayerAdvancedVampireEye(this));

        }
    }

    @Override
    protected ResourceLocation getEntityTexture(AdvancedVampireEntity entity) {
        return texture;
    }

    @Override
    protected void renderLivingLabel(AdvancedVampireEntity entityIn, String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 4);
    }


}
