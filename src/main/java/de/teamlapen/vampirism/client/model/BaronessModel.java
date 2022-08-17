package de.teamlapen.vampirism.client.model;


import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import org.jetbrains.annotations.NotNull;

/**
 * VampirismBaronLady - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronessModel extends AgeableListModel<VampireBaronEntity> implements HeadedModel, ArmedModel {

    private static final String BODY = "body";
    private static final String HEAD_OVERLAY = "head_overlay";
    private static final String LEG_RIGHT_OVERLAY = "leg_right_overlay";
    private static final String LEG_LEFT_OVERLAY = "leg_left_overlay";
    private static final String ARM_RIGHT_OVERLAY = "arm_right_overlay";
    private static final String ARM_LEFT_OVERLAY = "arm_left_overlay";
    private static final String BODY_OVERLAY = "body_overlay";
    private static final String HEAD = "head";
    private static final String ARM_RIGHT = "arm_right";
    private static final String ARM_LEFT = "arm_left";
    private static final String LEG_RIGHT = "leg_right";
    private static final String LEG_LEFT = "leg_left";
    private static final String CLAWS_RIGHT = "claws_right";
    private static final String CLAWS_LEFT = "claws_left";

    public final ModelPart body;
    public final ModelPart headOverlay;
    public final ModelPart legRightOverlay;
    public final ModelPart legLeftOverlay;
    public final ModelPart armRightOverlay;
    public final ModelPart bodyOverlay;
    public final ModelPart armLeftOverlay;
    public final ModelPart head;
    public final ModelPart armRight;
    public final ModelPart armLeft;
    public final ModelPart legRight;
    public final ModelPart legLeft;
    public final ModelPart clawsRight;
    public final ModelPart clawsLeft;

    protected final HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    protected final HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        CubeDeformation DEFORM_OVERLAY = new CubeDeformation(0.2f);
        PartDefinition body = part.addOrReplaceChild(BODY, CubeListBuilder.create().texOffs(16, 16).addBox(-4, 0, -2, 8, 12, 4), PartPose.ZERO);

        part.addOrReplaceChild(LEG_RIGHT_OVERLAY, CubeListBuilder.create().texOffs(0, 32).addBox(-2, 0, -2, 4, 12, 4, DEFORM_OVERLAY), PartPose.offset(-2, 12, 0));
        part.addOrReplaceChild(ARM_LEFT_OVERLAY, CubeListBuilder.create().texOffs(48, 48).addBox(0, -2, -2, 3, 12, 4, DEFORM_OVERLAY), PartPose.offset(4, 2, 0));
        body.addOrReplaceChild(LEG_LEFT, CubeListBuilder.create().texOffs(16, 48).addBox(-2, 0, -2, 4, 12, 4), PartPose.offset(2, 12, 0));
        part.addOrReplaceChild(HEAD_OVERLAY, CubeListBuilder.create().texOffs(32, 0).addBox(-4, -8, -4, 8, 8, 8, new CubeDeformation(0.5f)), PartPose.ZERO);
        PartDefinition armLeft = body.addOrReplaceChild(ARM_LEFT, CubeListBuilder.create().texOffs(32, 48).addBox(0, -1.5f, -2, 3, 12, 4), PartPose.offset(4, 2, 0));
        armLeft.addOrReplaceChild(CLAWS_LEFT, CubeListBuilder.create().texOffs(24, 0).addBox(-4, 0, -2, 3, 3, 4), PartPose.ZERO);
        body.addOrReplaceChild(HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-4, -8, -4, 8, 8, 8), PartPose.ZERO);
        PartDefinition armRight = body.addOrReplaceChild(ARM_RIGHT, CubeListBuilder.create().texOffs(40, 16).addBox(-3, -1.5f, -2, 3, 12, 4), PartPose.offset(-4, 2, 0));
        armRight.addOrReplaceChild(CLAWS_RIGHT, CubeListBuilder.create().texOffs(24, 0).addBox(1, -1, -2, 3, 3, 4), PartPose.ZERO);
        part.addOrReplaceChild(LEG_LEFT_OVERLAY, CubeListBuilder.create().texOffs(0, 48).addBox(-2, 0, -2, 4, 12, 4, DEFORM_OVERLAY), PartPose.offset(2, 12, 0));
        part.addOrReplaceChild(ARM_RIGHT_OVERLAY, CubeListBuilder.create().texOffs(40, 32).addBox(-3, -2, -2, 3, 12, 4, DEFORM_OVERLAY), PartPose.offset(-4, 2, 0));
        part.addOrReplaceChild(BODY_OVERLAY, CubeListBuilder.create().texOffs(16, 32).addBox(-4, 0, -2, 8, 12, 4, DEFORM_OVERLAY), PartPose.ZERO);
        body.addOrReplaceChild(LEG_RIGHT, CubeListBuilder.create().texOffs(0, 16).addBox(-2, 0, -2, 4, 12, 4), PartPose.offset(-2, 12, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public BaronessModel(ModelPart part) {

        this.body = part.getChild(BODY);
        this.headOverlay = part.getChild(HEAD_OVERLAY);
        this.legRightOverlay = part.getChild(LEG_RIGHT_OVERLAY);
        this.legLeftOverlay = part.getChild(LEG_LEFT_OVERLAY);
        this.armRightOverlay = part.getChild(ARM_RIGHT_OVERLAY);
        this.armLeftOverlay = part.getChild(ARM_LEFT_OVERLAY);
        this.bodyOverlay = part.getChild(BODY_OVERLAY);
        this.head = body.getChild(HEAD);
        this.armRight = body.getChild(ARM_RIGHT);
        this.armLeft = body.getChild(ARM_LEFT);
        this.legRight = body.getChild(LEG_RIGHT);
        this.legLeft = body.getChild(LEG_LEFT);
        this.clawsLeft = armLeft.getChild(CLAWS_LEFT);
        this.clawsRight = armRight.getChild(CLAWS_RIGHT);

    }

    @NotNull
    @Override
    public ModelPart getHead() {
        return head;
    }

    @Override
    public void setupAnim(@NotNull VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
            case EMPTY -> this.armLeft.yRot = 0.0F;
            case BLOCK -> {
                this.armLeft.xRot = this.armLeft.xRot * 0.5F - 0.9424779F;
                this.armLeft.yRot = ((float) Math.PI / 6F);
            }
            case ITEM -> {
                this.armLeft.xRot = this.armLeft.xRot * 0.5F - ((float) Math.PI / 10F);
                this.armLeft.yRot = 0.0F;
            }
        }

        switch (this.rightArmPose) {
            case EMPTY -> this.armRight.yRot = 0.0F;
            case BLOCK -> {
                this.armRight.xRot = this.armRight.xRot * 0.5F - 0.9424779F;
                this.armRight.yRot = (-(float) Math.PI / 6F);
            }
            case ITEM -> {
                this.armRight.xRot = this.armRight.xRot * 0.5F - ((float) Math.PI / 10F);
                this.armRight.yRot = 0.0F;
            }
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
    public void translateToHand(@NotNull HumanoidArm sideIn, @NotNull PoseStack matrixStackIn) {
        this.getArmForSide(sideIn).translateAndRotate(matrixStackIn);
    }

    protected ModelPart getArmForSide(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.armLeft : this.armRight;
    }

    @NotNull
    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.headOverlay, this.bodyOverlay, this.armLeftOverlay, this.armRightOverlay, this.legLeftOverlay, this.legRightOverlay);
    }

    protected HumanoidArm getSwingingSide(VampireBaronEntity entity) {
        HumanoidArm handside = entity.getMainArm();
        return entity.swingingArm == InteractionHand.MAIN_HAND ? handside : handside.getOpposite();
    }

    @NotNull
    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(head);
    }
}