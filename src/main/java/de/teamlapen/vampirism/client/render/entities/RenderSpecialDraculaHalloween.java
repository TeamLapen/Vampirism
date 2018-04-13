package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.ModelBipedCloaked;
import de.teamlapen.vampirism.client.render.LayerGlowingEyes;
import de.teamlapen.vampirism.entity.special.EntityDraculaHalloween;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderSpecialDraculaHalloween extends RenderLiving<EntityDraculaHalloween> {

    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/dracula.png");

    public RenderSpecialDraculaHalloween(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBipedCloaked(0, 0, 128, 64), 0.3F);
        this.addLayer(new LayerGlowingEyes<>(this, new ResourceLocation(REFERENCE.MODID, "textures/entity/dracula_eyes.png")).setBrightness(160f));
    }

    @Override
    public void doRender(EntityDraculaHalloween entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityDraculaHalloween entity) {
        return texture;
    }

    @Override
    protected void renderModel(EntityDraculaHalloween entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {

        if (entitylivingbaseIn.isParticle()) {
            ModelBiped model = (ModelBiped) getMainModel();
            model.setVisible(false);
            model.bipedHead.showModel = true;
            super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            model.setVisible(true);
        } else {
            super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        }
    }
}
