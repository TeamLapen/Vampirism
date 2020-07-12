package de.teamlapen.vampirism.client.model;


import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

/**
 * VampirismBaronLady - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronessModel extends EntityModel<VampireBaronEntity> implements IHasHead, IHasArm {
    public RendererModel body;
    public RendererModel headOverlay;
    public RendererModel legRightOverlay;
    public RendererModel legLeftOverlay;
    public RendererModel armRightOverlay;
    public RendererModel bodyOverlay;
    public RendererModel armLeftOverlay;
    public RendererModel head;
    public RendererModel armRight;
    public RendererModel armLeft;
    public RendererModel legRight;
    public RendererModel legLeft;
    public RendererModel clawsRight;
    public RendererModel clawsLeft;

    protected BipedModel.ArmPose leftArmPose = BipedModel.ArmPose.EMPTY;
    protected BipedModel.ArmPose rightArmPose = BipedModel.ArmPose.EMPTY;

    public BaronessModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.legRightOverlay = new RendererModel(this, 0, 32);
        this.legRightOverlay.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.legRightOverlay.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.armLeftOverlay = new RendererModel(this, 48, 48);
        this.armLeftOverlay.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.armLeftOverlay.addBox(0.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.legLeft = new RendererModel(this, 16, 48);
        this.legLeft.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.headOverlay = new RendererModel(this, 32, 0);
        this.headOverlay.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headOverlay.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
        this.clawsLeft = new RendererModel(this, 24, 0);
        this.clawsLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawsLeft.addBox(-4.0F, 0F, -2.0F, 3, 3, 4, 0.0F);
        this.head = new RendererModel(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.clawsRight = new RendererModel(this, 24, 0);
        this.clawsRight.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawsRight.addBox(1.0F, -1F, -2.0F, 3, 3, 4, 0.0F);
        this.legLeftOverlay = new RendererModel(this, 0, 48);
        this.legLeftOverlay.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.legLeftOverlay.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.armRight = new RendererModel(this, 40, 16);
        this.armRight.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.armRight.addBox(-3.0F, -1.5F, -2.0F, 3, 12, 4, 0.0F);
        this.armRightOverlay = new RendererModel(this, 40, 32);
        this.armRightOverlay.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.armRightOverlay.addBox(-3.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.armLeft = new RendererModel(this, 32, 48);
        this.armLeft.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.armLeft.addBox(0.0F, -1.5F, -2.0F, 3, 12, 4, 0.0F);
        this.bodyOverlay = new RendererModel(this, 16, 32);
        this.bodyOverlay.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyOverlay.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F);
        this.body = new RendererModel(this, 16, 16);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.legRight = new RendererModel(this, 0, 16);
        this.legRight.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.body.addChild(this.legLeft);
        this.armLeft.addChild(this.clawsLeft);
        this.body.addChild(this.head);
        this.armRight.addChild(this.clawsRight);
        this.body.addChild(this.armRight);
        this.body.addChild(this.armLeft);
        this.body.addChild(this.legRight);
    }

    @Override
    public RendererModel func_205072_a() {
        return head;
    }

    @Override
    public void postRenderArm(float scale, HandSide side) {
        this.getArmForSide(side).postRender(scale);
    }

    @Override
    public void render(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.legRightOverlay.render(scale);
        this.armLeftOverlay.render(scale);
        this.headOverlay.render(scale);
        this.legLeftOverlay.render(scale);
        this.armRightOverlay.render(scale);
        this.bodyOverlay.render(scale);
        this.body.render(scale);
    }

    @Override
    public void setRotationAngles(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180f);

        this.body.rotateAngleY = 0.0F;
        this.armRight.rotationPointZ = 0.0F;
        this.armRight.rotationPointX = -4.0F;
        this.armLeft.rotationPointZ = 0.0F;
        this.armLeft.rotationPointX = 4.0F;


        this.armRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.armLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.armRight.rotateAngleZ = 0.0F;
        this.armLeft.rotateAngleZ = 0.0F;
        this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.legRight.rotateAngleY = 0.0F;
        this.legLeft.rotateAngleY = 0.0F;
        this.legRight.rotateAngleZ = 0.0F;
        this.legLeft.rotateAngleZ = 0.0F;


        this.armRight.rotateAngleY = 0.0F;
        this.armRight.rotateAngleZ = 0.0F;

        switch (this.leftArmPose) {
            case EMPTY:
                this.armLeft.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.armLeft.rotateAngleX = this.armLeft.rotateAngleX * 0.5F - 0.9424779F;
                this.armLeft.rotateAngleY = ((float) Math.PI / 6F);
                break;
            case ITEM:
                this.armLeft.rotateAngleX = this.armLeft.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
                this.armLeft.rotateAngleY = 0.0F;
        }

        switch (this.rightArmPose) {
            case EMPTY:
                this.armRight.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.armRight.rotateAngleX = this.armRight.rotateAngleX * 0.5F - 0.9424779F;
                this.armRight.rotateAngleY = (-(float) Math.PI / 6F);
                break;
            case ITEM:
                this.armRight.rotateAngleX = this.armRight.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
                this.armRight.rotateAngleY = 0.0F;
                break;
        }
        if (this.swingProgress > 0.0F) {
            HandSide handside = this.getSwingingSide(entityIn);
            RendererModel renderermodel = this.getArmForSide(handside);
            float f1 = this.swingProgress;
            this.body.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                this.body.rotateAngleY *= -1.0F;
            }
            //Claw rotations are broken
//            this.armRight.rotationPointZ = MathHelper.sin(this.body.rotateAngleY) * 5.0F;
            this.armRight.rotationPointX = -MathHelper.cos(this.body.rotateAngleY) * 4.0F;
//            this.armLeft.rotationPointZ = -MathHelper.sin(this.body.rotateAngleY) * 5.0F;
            this.armLeft.rotationPointX = MathHelper.cos(this.body.rotateAngleY) * 4.0F;
//            this.armRight.rotateAngleY += this.body.rotateAngleY;
//            this.armLeft.rotateAngleY += this.body.rotateAngleY;
            this.armLeft.rotateAngleX += this.body.rotateAngleY;
            f1 = 1.0F - this.swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float) Math.PI);
            float f3 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.head.rotateAngleX - 0.7F) * 0.75F;
            renderermodel.rotateAngleX = (float) ((double) renderermodel.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
//            renderermodel.rotateAngleY += this.body.rotateAngleY * 2.0F;
//            renderermodel.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
        }

        this.body.rotateAngleX = 0.0F;
        this.legRight.rotationPointZ = 0.1F;
        this.legLeft.rotationPointZ = 0.1F;
        this.legRight.rotationPointY = 12.0F;
        this.legLeft.rotationPointY = 12.0F;
        this.head.rotationPointY = 0.0F;


        this.armRight.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.06F - 0.03;
        this.armLeft.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.06F + 0.03;


        this.headOverlay.copyModelAngles(this.head);
        this.armLeftOverlay.copyModelAngles(this.armLeft);
        this.armRightOverlay.copyModelAngles(this.armRight);
        this.clawsLeft.copyModelAngles(this.armLeft);
        this.clawsLeft.rotationPointY += 8.5;
        this.clawsRight.copyModelAngles(this.armRight);
        this.clawsRight.rotationPointY += 9.5;

    }

    protected RendererModel getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.armLeft : this.armRight;
    }

    protected HandSide getSwingingSide(VampireBaronEntity entity) {
        HandSide handside = entity.getPrimaryHand();
        return entity.swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
    }
}
