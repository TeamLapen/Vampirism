package de.teamlapen.vampirism.client.model;


import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

/**
 * Attire designed for the female vampire baroness - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronessAttireModel<T extends LivingEntity> extends EntityModel<T> {
    public RendererModel dressTorso;
    public RendererModel dressArmBandRight;
    public RendererModel dressArmBandLeft;
    public RendererModel hat;
    public RendererModel hood;

    public RendererModel dressCurtain;
    public RendererModel hat2;
    public RendererModel veil;
    public RendererModel cloak;

    public BaronessAttireModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.veil = new RendererModel(this, 32, 28);
        this.veil.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.veil.addBox(-4.5F, -8.5F, -4.5F, 9, 9, 9, 0.0F);
        this.dressArmBandLeft = new RendererModel(this, 60, 46);
        this.dressArmBandLeft.mirror = true;
        this.dressArmBandLeft.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.dressArmBandLeft.addBox(0.0F, 2.0F, -2.0F, 3, 3, 4, 0.5F);
        this.dressCurtain = new RendererModel(this, 64, 43);
        this.dressCurtain.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.dressCurtain.addBox(-6.0F, 0.0F, -4.0F, 12, 11, 10, 0.0F);
        this.hood = new RendererModel(this, 44, 0);
        this.hood.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hood.addBox(-4.5F, -8.5F, -4.0F, 9, 9, 9, 0.0F);

        this.hat2 = new RendererModel(this, 72, 30);
        this.hat2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat2.addBox(-2.0F, -11.0F, -2.0F, 4, 2, 4, 0.0F);
        this.dressTorso = new RendererModel(this, 36, 46);
        this.dressTorso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.dressTorso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.4F);
        this.hat = new RendererModel(this, 68, 36);
        this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-3.0F, -9.0F, -3.0F, 6, 1, 6, 0.0F);

        this.cloak = new RendererModel(this, 0, 0);
        this.cloak.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cloak.addBox(-8.5F, -1.0F, -2.5F, 17, 22, 5, 0.0F);
        this.setRotateAngle(cloak, 0.3141592653589793F, 0.0F, 0.0F);
        this.dressArmBandRight = new RendererModel(this, 60, 46);
        this.dressArmBandRight.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.dressArmBandRight.addBox(-3.0F, 2.0F, -2.0F, 3, 3, 4, 0.5F);
        this.hat.addChild(this.veil);
        this.dressTorso.addChild(this.dressCurtain);
        this.hat.addChild(this.hat2);
        this.hood.addChild(this.cloak);
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.dressArmBandLeft.render(scale);
        this.hood.render(scale);
        this.dressTorso.render(scale);
        this.hat.render(scale);
        this.dressArmBandRight.render(scale);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(RendererModel RendererModel, float x, float y, float z) {
        RendererModel.rotateAngleX = x;
        RendererModel.rotateAngleY = y;
        RendererModel.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        float bodyRotateY = 0;
        float headRotateY = 0;
        headRotateY = netHeadYaw * ((float) Math.PI / 180f);
        if (this.swingProgress > 0.0F) {
            HandSide handside = this.getSwingingSide(entityIn);
            float f1 = this.swingProgress;
            bodyRotateY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                bodyRotateY *= -1.0F;
            }
        }

        this.hat.rotateAngleY = bodyRotateY + headRotateY;
        this.veil.rotateAngleY = bodyRotateY + headRotateY;
        this.hood.rotateAngleY = bodyRotateY + headRotateY;
        this.hat2.rotateAngleY = bodyRotateY + headRotateY;
        this.cloak.rotateAngleY = bodyRotateY;
        this.dressCurtain.rotateAngleY = bodyRotateY;
        this.dressTorso.rotateAngleY = bodyRotateY;
        this.dressArmBandLeft.rotateAngleY = bodyRotateY;
        this.dressArmBandRight.rotateAngleY = bodyRotateY;
    }

    protected HandSide getSwingingSide(T entity) {
        HandSide handside = entity.getPrimaryHand();
        return entity.swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
    }
}
