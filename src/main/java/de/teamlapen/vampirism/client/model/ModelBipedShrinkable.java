package de.teamlapen.vampirism.client.model;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * ModelBiped which can grow/shrink between mature and child size
 *
 * @author Maxanier
 */
@OnlyIn(Dist.CLIENT)
public class ModelBipedShrinkable<T extends LivingEntity> extends BipedModel<T> {

    private float size = 1F;

    public ModelBipedShrinkable() {
        super();
    }

    public ModelBipedShrinkable(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
        super(modelSize, p_i1149_2_, textureWidthIn, textureHeightIn);
    }

    @Override
    public void render(T p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        this.setRotationAngles(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);

        float f6 = 2.0F - size;
        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.5F / (1.5F + (1F - size) * 0.5F), 1.5F / (1.5F + (1F - size) * 0.5F), 1.5F / (1.5F + (1F - size) * 0.5F));
        GlStateManager.translatef(0.0F, 16.0F * p_78088_7_ * (-(size) * (size - 1F) - size + 1), 0.0F);
        this.bipedHead.render(p_78088_7_);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
        GlStateManager.translatef(0.0F, 24.0F * p_78088_7_ * (1F - size), 0.0F);
        this.bipedBody.render(p_78088_7_);
        this.bipedRightArm.render(p_78088_7_);
        this.bipedLeftArm.render(p_78088_7_);
        this.bipedRightLeg.render(p_78088_7_);
        this.bipedLeftLeg.render(p_78088_7_);

        this.bipedHeadwear.render(p_78088_7_);
        GlStateManager.popMatrix();
    }

    /**
     * Sets shrink status 1 equals mature size, 0 equals child size
     *
     * @param f
     */
    public void setSize(float f) {
        if (f > 1) {
            size = 1.0F;
        } else if (f < 0) {
            f = 0;
        } else {
            size = f;
        }

    }
}