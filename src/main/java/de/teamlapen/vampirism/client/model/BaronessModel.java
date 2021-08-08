package de.teamlapen.vampirism.client.model;


import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;

/**
 * VampirismBaronLady - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronessModel extends AgeableListModel<VampireBaronEntity> implements HeadedModel, ArmedModel {
    public ModelPart body;
    public ModelPart headOverlay;
    public ModelPart legRightOverlay;
    public ModelPart legLeftOverlay;
    public ModelPart armRightOverlay;
    public ModelPart bodyOverlay;
    public ModelPart armLeftOverlay;
    public ModelPart head;
    public ModelPart armRight;
    public ModelPart armLeft;
    public ModelPart legRight;
    public ModelPart legLeft;
    public ModelPart clawsRight;
    public ModelPart clawsLeft;

    protected HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    protected HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;

    public BaronessModel() {
        this.texWidth = 64;
        this.texHeight = 64;
        this.legRightOverlay = new ModelPart(this, 0, 32);
        this.legRightOverlay.setPos(-2.0F, 12.0F, 0.0F);
        this.legRightOverlay.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.armLeftOverlay = new ModelPart(this, 48, 48);
        this.armLeftOverlay.setPos(4.0F, 2.0F, 0.0F);
        this.armLeftOverlay.addBox(0.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.legLeft = new ModelPart(this, 16, 48);
        this.legLeft.setPos(2.0F, 12.0F, 0.0F);
        this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.headOverlay = new ModelPart(this, 32, 0);
        this.headOverlay.setPos(0.0F, 0.0F, 0.0F);
        this.headOverlay.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
        this.clawsLeft = new ModelPart(this, 24, 0);
        this.clawsLeft.setPos(0.0F, 0.0F, 0.0F);
        this.clawsLeft.addBox(-4.0F, 0F, -2.0F, 3, 3, 4, 0.0F);
        this.head = new ModelPart(this, 0, 0);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.clawsRight = new ModelPart(this, 24, 0);
        this.clawsRight.setPos(0.0F, 0.0F, 0.0F);
        this.clawsRight.addBox(1.0F, -1F, -2.0F, 3, 3, 4, 0.0F);
        this.legLeftOverlay = new ModelPart(this, 0, 48);
        this.legLeftOverlay.setPos(2.0F, 12.0F, 0.0F);
        this.legLeftOverlay.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.armRight = new ModelPart(this, 40, 16);
        this.armRight.setPos(-4.0F, 2.0F, 0.0F);
        this.armRight.addBox(-3.0F, -1.5F, -2.0F, 3, 12, 4, 0.0F);
        this.armRightOverlay = new ModelPart(this, 40, 32);
        this.armRightOverlay.setPos(-4.0F, 2.0F, 0.0F);
        this.armRightOverlay.addBox(-3.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.armLeft = new ModelPart(this, 32, 48);
        this.armLeft.setPos(4.0F, 2.0F, 0.0F);
        this.armLeft.addBox(0.0F, -1.5F, -2.0F, 3, 12, 4, 0.0F);
        this.bodyOverlay = new ModelPart(this, 16, 32);
        this.bodyOverlay.setPos(0.0F, 0.0F, 0.0F);
        this.bodyOverlay.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F);
        this.body = new ModelPart(this, 16, 16);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.legRight = new ModelPart(this, 0, 16);
        this.legRight.setPos(-2.0F, 12.0F, 0.0F);
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
    public ModelPart getHead() {
        return head;
    }

    @Override
    public void setupAnim(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180f);

        this.body.yRot = 0.0F;
        this.armRight.z = 0.0F;
        this.armRight.x = -4.0F;
        this.armLeft.z = 0.0F;
        this.armLeft.x = 4.0F;


        this.armRight.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.armLeft.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.armRight.zRot = 0.0F;
        this.armLeft.zRot = 0.0F;
        this.legRight.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legLeft.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.legRight.yRot = 0.0F;
        this.legLeft.yRot = 0.0F;
        this.legRight.zRot = 0.0F;
        this.legLeft.zRot = 0.0F;


        this.armRight.yRot = 0.0F;
        this.armRight.zRot = 0.0F;

        switch (this.leftArmPose) {
            case EMPTY:
                this.armLeft.yRot = 0.0F;
                break;
            case BLOCK:
                this.armLeft.xRot = this.armLeft.xRot * 0.5F - 0.9424779F;
                this.armLeft.yRot = ((float) Math.PI / 6F);
                break;
            case ITEM:
                this.armLeft.xRot = this.armLeft.xRot * 0.5F - ((float) Math.PI / 10F);
                this.armLeft.yRot = 0.0F;
        }

        switch (this.rightArmPose) {
            case EMPTY:
                this.armRight.yRot = 0.0F;
                break;
            case BLOCK:
                this.armRight.xRot = this.armRight.xRot * 0.5F - 0.9424779F;
                this.armRight.yRot = (-(float) Math.PI / 6F);
                break;
            case ITEM:
                this.armRight.xRot = this.armRight.xRot * 0.5F - ((float) Math.PI / 10F);
                this.armRight.yRot = 0.0F;
                break;
        }
        if (this.attackTime > 0.0F) {
            HumanoidArm handside = this.getSwingingSide(entityIn);
            ModelPart ModelRenderer = this.getArmForSide(handside);
            float f1 = this.attackTime;
            this.body.yRot = Mth.sin(Mth.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HumanoidArm.LEFT) {
                this.body.yRot *= -1.0F;
            }
            //Claw rotations are broken
//            this.armRight.rotationPointZ = MathHelper.sin(this.body.rotateAngleY) * 5.0F;
            this.armRight.x = -Mth.cos(this.body.yRot) * 4.0F;
//            this.armLeft.rotationPointZ = -MathHelper.sin(this.body.rotateAngleY) * 5.0F;
            this.armLeft.x = Mth.cos(this.body.yRot) * 4.0F;
//            this.armRight.rotateAngleY += this.body.rotateAngleY;
//            this.armLeft.rotateAngleY += this.body.rotateAngleY;
            this.armLeft.xRot += this.body.yRot;
            f1 = 1.0F - this.attackTime;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = Mth.sin(f1 * (float) Math.PI);
            float f3 = Mth.sin(this.attackTime * (float) Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
            ModelRenderer.xRot = (float) ((double) ModelRenderer.xRot - ((double) f2 * 1.2D + (double) f3));
//            ModelRenderer.rotateAngleY += this.body.rotateAngleY * 2.0F;
//            ModelRenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
        }

        this.body.xRot = 0.0F;
        this.legRight.z = 0.1F;
        this.legLeft.z = 0.1F;
        this.legRight.y = 12.0F;
        this.legLeft.y = 12.0F;
        this.head.y = 0.0F;


        this.armRight.xRot += Mth.sin(ageInTicks * 0.067F) * 0.06F - 0.03;
        this.armLeft.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.06F + 0.03;


        this.headOverlay.copyFrom(this.head);
        this.armLeftOverlay.copyFrom(this.armLeft);
        this.armRightOverlay.copyFrom(this.armRight);
        this.clawsLeft.copyFrom(this.armLeft);
        this.clawsLeft.y += 8.5;
        this.clawsRight.copyFrom(this.armRight);
        this.clawsRight.y += 9.5;
    }

    @Override
    public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
        this.getArmForSide(sideIn).translateAndRotate(matrixStackIn);
    }

    protected ModelPart getArmForSide(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.armLeft : this.armRight;
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.headOverlay, this.bodyOverlay, this.armLeftOverlay, this.armRightOverlay, this.legLeftOverlay, this.legRightOverlay);
    }

    protected HumanoidArm getSwingingSide(VampireBaronEntity entity) {
        HumanoidArm handside = entity.getMainArm();
        return entity.swingingArm == InteractionHand.MAIN_HAND ? handside : handside.getOpposite();
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(head);
    }
}