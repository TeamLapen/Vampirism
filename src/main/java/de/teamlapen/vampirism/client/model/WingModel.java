package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;


public class WingModel<T extends LivingEntity> extends AgeableModel<T> {
    public ModelRenderer wingRight;
    public ModelRenderer wingLeft;
    public ModelRenderer wingRight2;
    public ModelRenderer wingLeft2;

    public WingModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.wingRight = new ModelRenderer(this, 0, 46);
        this.wingRight.setRotationPoint(0.2F, 2.5F, 2.0F);
        this.wingRight.addBox(-18.0F, -6.0F, 0.0F, 18, 18, 0, 0.0F);

        this.setRotateAngle(wingRight, 0.136659280431156F, 0.5462880558742251F, 0.27314402793711257F);
        this.wingLeft2 = new ModelRenderer(this, 0, 28);
        this.wingLeft2.mirror = true;
        this.wingLeft2.setRotationPoint(18.0F, -2.0F, 0.0F);
        this.wingLeft2.addBox(0.0F, -4.0F, 0.0F, 16, 18, 0, 0.0F);
        this.setRotateAngle(wingLeft2, 0.0F, 0.8196066167365371F, 0.0F);
        this.wingLeft = new ModelRenderer(this, 0, 46);
        this.wingLeft.mirror = true;
        this.wingLeft.setRotationPoint(-0.2F, 2.5F, 2.0F);
        this.wingLeft.addBox(0.0F, -6.0F, 0.0F, 18, 18, 0, 0.0F);
        this.setRotateAngle(wingLeft, 0.136659280431156F, -0.6373942428283291F, -0.27314402793711257F);
        this.wingRight2 = new ModelRenderer(this, 0, 28);
        this.wingRight2.setRotationPoint(-18.0F, -2.0F, 0.0F);
        this.wingRight2.addBox(-16.0F, -4.0F, 0.0F, 16, 18, 0, 0.0F);
        this.setRotateAngle(wingRight2, 0.0F, -0.8196066167365371F, 0.0F);
        this.wingRight.addChild(this.wingRight2);
        this.wingLeft.addChild(this.wingLeft2);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return Collections.emptyList();
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.wingLeft,this.wingRight);
    }

    public void copyRotationFromBody(ModelRenderer body) {
        this.wingLeft.rotateAngleY = body.rotateAngleY;
        this.wingLeft2.rotateAngleY = body.rotateAngleY;
        this.wingRight.rotateAngleY = body.rotateAngleY;
        this.wingRight2.rotateAngleY = body.rotateAngleY;
        this.wingLeft.rotateAngleX = body.rotateAngleX;
        this.wingRight.rotateAngleX = body.rotateAngleX;
        this.wingLeft.rotateAngleZ = body.rotateAngleZ;
        this.wingRight.rotateAngleZ = body.rotateAngleZ;
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        if (entityIn.isShiftKeyDown()) {
            this.wingRight.rotationPointY = 3.0f;
            this.wingLeft.rotationPointY = 3.0f;
        } else {
            this.wingRight.rotationPointY = 2.5f;
            this.wingLeft.rotationPointY = 2.5f;
        }

        this.wingLeft.rotateAngleZ -= MathHelper.cos((entityIn.ticksExisted+partialTick) * 0.0662F + (float) Math.PI) * 0.06;
        this.wingRight.rotateAngleZ += MathHelper.cos((entityIn.ticksExisted+partialTick) * 0.0662F + (float) Math.PI) * 0.06;

        this.wingLeft.rotateAngleY -= 0.3f;
        this.wingRight.rotateAngleY += 0.3f;
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
        ModelRenderer.rotateAngleX = x;
        ModelRenderer.rotateAngleY = y;
        ModelRenderer.rotateAngleZ = z;
    }

    protected HandSide getSwingingSide(T entity) {
        HandSide handside = entity.getPrimaryHand();
        return entity.swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
    }
}