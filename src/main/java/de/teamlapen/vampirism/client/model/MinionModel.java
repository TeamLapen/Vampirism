package de.teamlapen.vampirism.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;


public class MinionModel<T extends MinionEntity> extends PlayerModel<T> {
    public MinionModel(float p_i46304_1_, boolean p_i46304_2_) {
        super(p_i46304_1_, p_i46304_2_);
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        float s = entityIn.getScale();
        float off = (1 - s) * 1.95f;
        GlStateManager.pushMatrix();
        GlStateManager.scalef(s, s, s);
        GlStateManager.translatef(0.0F, off, 0.0F);
        this.bipedHead.render(scale);
        this.bipedHeadwear.render(scale);

        GlStateManager.popMatrix();

        if (!entityIn.shouldRenderLordSkin()) {
            this.renderBody(entityIn, scale);
        }
    }

    public void renderBody(T entityIn, float scale) {
        float s = entityIn.getScale();
        float off = (1 - s) * 1.95f;

        GlStateManager.pushMatrix();
        GlStateManager.scalef(s, s, s);
        GlStateManager.translatef(0.0F, off, 0.0F);
        this.bipedBody.render(scale);
        this.bipedRightArm.render(scale);
        this.bipedLeftArm.render(scale);
        this.bipedRightLeg.render(scale);
        this.bipedLeftLeg.render(scale);
        this.bipedLeftLegwear.render(scale);
        this.bipedRightLegwear.render(scale);
        this.bipedLeftArmwear.render(scale);
        this.bipedRightArmwear.render(scale);
        this.bipedBodyWear.render(scale);


        GlStateManager.popMatrix();
    }
}
