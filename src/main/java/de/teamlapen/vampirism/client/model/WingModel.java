package de.teamlapen.vampirism.client.model;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

/**
 * 1.14
 *
 * @author maxanier
 */
public class WingModel<T extends LivingEntity> extends EntityModel<T> {
    public RendererModel wingRight;
    public RendererModel wingLeft;
    public RendererModel wingRight2;
    public RendererModel wingLeft2;

    public WingModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.wingRight = new RendererModel(this, 0, 46);
        this.wingRight.setRotationPoint(0.0F, 4.0F, 2.0F);
        this.wingRight.addBox(-18.0F, -6.0F, 0.0F, 18, 18, 0, 0.0F);

        this.setRotateAngle(wingRight, 0.136659280431156F, 0.5462880558742251F, 0.27314402793711257F);
        this.wingLeft2 = new RendererModel(this, 0, 28);
        this.wingLeft2.mirror = true;
        this.wingLeft2.setRotationPoint(18.0F, -2.0F, 0.0F);
        this.wingLeft2.addBox(0.0F, -4.0F, 0.0F, 16, 18, 0, 0.0F);
        this.setRotateAngle(wingLeft2, 0.0F, 0.8196066167365371F, 0.0F);
        this.wingLeft = new RendererModel(this, 0, 46);
        this.wingLeft.mirror = true;
        this.wingLeft.setRotationPoint(0.0F, 4.0F, 2.0F);
        this.wingLeft.addBox(0.0F, -6.0F, 0.0F, 18, 18, 0, 0.0F);
        this.setRotateAngle(wingLeft, 0.136659280431156F, -0.6373942428283291F, -0.27314402793711257F);
        this.wingRight2 = new RendererModel(this, 0, 28);
        this.wingRight2.setRotationPoint(-18.0F, -2.0F, 0.0F);
        this.wingRight2.addBox(-16.0F, -4.0F, 0.0F, 16, 18, 0, 0.0F);
        this.setRotateAngle(wingRight2, 0.0F, -0.8196066167365371F, 0.0F);
        this.wingRight.addChild(this.wingRight2);
        this.wingLeft.addChild(this.wingLeft2);
    }

    public void copyRotationFromBody(RendererModel body) {
        this.wingLeft.rotateAngleY = body.rotateAngleY;
        this.wingLeft2.rotateAngleY = body.rotateAngleY;
        this.wingRight.rotateAngleY = body.rotateAngleY;
        this.wingRight2.rotateAngleY = body.rotateAngleY;
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.wingRight.render(scale);
        this.wingLeft.render(scale);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(RendererModel RendererModel, float x, float y, float z) {
        RendererModel.rotateAngleX = x;
        RendererModel.rotateAngleY = y;
        RendererModel.rotateAngleZ = z;
    }

    protected HandSide getSwingingSide(T entity) {
        HandSide handside = entity.getPrimaryHand();
        return entity.swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
    }
}
