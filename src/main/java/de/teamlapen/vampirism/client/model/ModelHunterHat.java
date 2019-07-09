package de.teamlapen.vampirism.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ModelHunterHat<T extends LivingEntity> extends BipedModel<T> {
    public static final ModelHunterHat hat0 = new ModelHunterHat(0);
    public static final ModelHunterHat hat1 = new ModelHunterHat(1);
    private RendererModel hatTop;
    private RendererModel hatRim;


    public ModelHunterHat(int type) {
        super(0.0F, 0.0F, 64, 64);
        if (type == 0) {
            hatTop = new RendererModel(this, 0, 31);
            hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
            hatTop.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
            hatTop.setTextureSize(128, 64);
            hatTop.mirror = true;

            hatRim = new RendererModel(this, 0, 35);
            hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
            hatRim.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
            hatRim.setTextureSize(128, 64);
            hatRim.mirror = true;
        } else if (type == 1) {
            hatTop = new RendererModel(this, 0, 31);
            hatTop.addBox(-4F, -12F, -4F, 8, 3, 8);
            hatTop.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
            hatTop.setTextureSize(128, 64);
            hatTop.mirror = true;

            hatRim = new RendererModel(this, 0, 31);
            hatRim.addBox(-8F, -9F, -8F, 16, 1, 16);
            hatRim.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
            hatRim.setTextureSize(128, 64);
            hatRim.mirror = true;
        }

    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        hatTop.render(scale);
        hatRim.render(scale);
    }

    @Override
    public void setRotationAngles(T e, float f1, float f2, float f3, float f4, float f5, float f6) {
        super.setRotationAngles(e, f1, f2, f3, f4, f5, f6);
        hatRim.rotateAngleX = super.bipedHead.rotateAngleX;
        hatRim.rotateAngleY = super.bipedHead.rotateAngleY;
        hatRim.rotateAngleZ = super.bipedHead.rotateAngleZ;
        hatTop.rotateAngleX = super.bipedHead.rotateAngleX;
        hatTop.rotateAngleY = super.bipedHead.rotateAngleY;
        hatTop.rotateAngleZ = super.bipedHead.rotateAngleZ;
    }

    @Override
    public void setVisible(boolean invisible) {
        super.setVisible(false);
        hatRim.showModel = true;
        hatTop.showModel = true;
    }
}
