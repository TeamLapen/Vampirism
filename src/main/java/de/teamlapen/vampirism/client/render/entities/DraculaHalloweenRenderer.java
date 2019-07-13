package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.BipedCloakedModel;
import de.teamlapen.vampirism.client.render.LayerGlowingEyes;
import de.teamlapen.vampirism.entity.special.DraculaHalloweenEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class DraculaHalloweenRenderer extends MobRenderer<DraculaHalloweenEntity, BipedCloakedModel<DraculaHalloweenEntity>> {

    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/dracula.png");

    public DraculaHalloweenRenderer(EntityRendererManager rendermanagerIn) {
        super(rendermanagerIn, new BipedCloakedModel(0, 0, 128, 64), 0.3F);
        this.addLayer(new LayerGlowingEyes(this, new ResourceLocation(REFERENCE.MODID, "textures/entity/dracula_eyes.png")).setBrightness(160f));
    }

    @Override
    public void doRender(DraculaHalloweenEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(DraculaHalloweenEntity entity) {
        return texture;
    }

    @Override
    protected void renderModel(DraculaHalloweenEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {

        if (entitylivingbaseIn.isParticle()) {
            BipedModel model = getEntityModel();
            model.setVisible(false);
            model.bipedHead.showModel = true;
            super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            model.setVisible(true);
        } else {
            super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        }
    }
}
