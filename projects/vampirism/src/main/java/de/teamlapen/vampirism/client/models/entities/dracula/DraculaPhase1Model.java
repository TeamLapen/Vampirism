package de.teamlapen.vampirism.client.models.entities.dracula;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entities.DraculaRenderer;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;

public class DraculaPhase1Model extends DraculaModel {

    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation idleAnimation;

    public DraculaPhase1Model(ModelPart root) {
        super(root);
        this.walkAnimation = DraculaAnimations.Phase1.WALK.bake(root);
        this.idleAnimation = DraculaAnimations.Phase1.IDLE.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -12.0F, -1.0F, -0.0262F, 0.0F, 0.0F));
        PartDefinition upper_body = body.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -14.0F, 4.0F, -0.0524F, 0.0F, 0.0F));
        PartDefinition upper_body_shirt = upper_body.addOrReplaceChild("upper_body_shirt", CubeListBuilder.create().texOffs(92, 26).addBox(-6.0F, -8.0F, -6.2F, 12.0F, 8.0F, 6.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition upper_body_shirt_cloak = upper_body_shirt.addOrReplaceChild("upper_body_shirt_cloak", CubeListBuilder.create().texOffs(92, 58).addBox(-6.0F, -8.0F, -6.2F, 12.0F, 8.0F, 6.0F, new CubeDeformation(1.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cloak_collar = upper_body.addOrReplaceChild("cloak_collar", CubeListBuilder.create().texOffs(66, 0).addBox(-7.0F, -8.0F, 0.0F, 14.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 1.0F, -0.1745F, 0.0F, 0.0F));
        PartDefinition cloak_collar_right = cloak_collar.addOrReplaceChild("cloak_collar_right", CubeListBuilder.create().texOffs(80, 4).addBox(-7.0F, -8.0F, -8.0F, 0.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cloak_collar_left = cloak_collar.addOrReplaceChild("cloak_collar_left", CubeListBuilder.create().texOffs(64, 4).addBox(7.0F, -8.0F, -8.0F, 0.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bow_tie = upper_body.addOrReplaceChild("bow_tie", CubeListBuilder.create().texOffs(104, 16).addBox(-1.0F, -1.0F, -0.1266F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -7.5F));
        PartDefinition bow_tie_right = bow_tie.addOrReplaceChild("bow_tie_right", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bow_tie_right_r1 = bow_tie_right.addOrReplaceChild("bow_tie_right_r1", CubeListBuilder.create().texOffs(108, 18).addBox(-0.442F, -1.0F, -0.0056F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.141F, 0.0849F, 0.3929F));
        PartDefinition bow_tie_left = bow_tie.addOrReplaceChild("bow_tie_left", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bow_tie_left_r1 = bow_tie_left.addOrReplaceChild("bow_tie_left_r1", CubeListBuilder.create().texOffs(96, 18).addBox(-5.558F, -1.0F, -0.0055F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.141F, -0.0849F, -0.3929F));
        PartDefinition neck = upper_body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(120, 16).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -9.0F, -6.0F, -0.1309F, 0.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 1.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition lower_body = body.addOrReplaceChild("lower_body", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition lower_body_left = lower_body.addOrReplaceChild("lower_body_left", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition ShirtLeft_r1 = lower_body_left.addOrReplaceChild("ShirtLeft_r1", CubeListBuilder.create().texOffs(80, 40).addBox(-0.3F, -12.0F, -2.31F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.9F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0175F));
        PartDefinition lower_body_left_cloak = lower_body_left.addOrReplaceChild("lower_body_left_cloak", CubeListBuilder.create().texOffs(80, 72).addBox(0.0F, -12.01F, -2.31F, 6.0F, 12.0F, 6.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition lower_body_right = lower_body.addOrReplaceChild("lower_body_right", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition ShirtRight_r1 = lower_body_right.addOrReplaceChild("ShirtRight_r1", CubeListBuilder.create().texOffs(104, 40).addBox(-5.7F, -12.0F, -2.3F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.9F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0175F));
        PartDefinition lower_body_right_cloak = lower_body_right.addOrReplaceChild("lower_body_right_cloak", CubeListBuilder.create().texOffs(104, 72).addBox(-6.0F, -12.01F, -2.3F, 6.0F, 12.0F, 6.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(40, 0).addBox(0.0F, -7.0F, -3.0F, 6.0F, 44.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, -0.2F, 0.0436F, 0.0F, -0.0087F));
        PartDefinition left_leg_cloak_top = left_leg.addOrReplaceChild("left_leg_cloak_top", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg_cloak_top_r1 = left_leg_cloak_top.addOrReplaceChild("left_leg_cloak_top_r1", CubeListBuilder.create().texOffs(32, 50).addBox(0.0F, 7.99F, -3.01F, 6.0F, 24.0F, 6.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, 0.0087F));
        PartDefinition left_leg_cloak_front = left_leg.addOrReplaceChild("left_leg_cloak_front", CubeListBuilder.create().texOffs(56, 102).addBox(-0.2F, -0.5F, -3.21F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.9F)), PartPose.offsetAndRotation(0.0F, 19.0F, 0.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition left_leg_cloak_side = left_leg.addOrReplaceChild("left_leg_cloak_side", CubeListBuilder.create().texOffs(32, 80).addBox(-0.2F, 0.54F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, 18.0F, 0.0F, 0.0873F, 0.0F, -0.096F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(40, 0).addBox(-6.0F, -7.0F, -3.0F, 6.0F, 44.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, -0.2F, 0.0436F, 0.0F, 0.0087F));
        PartDefinition right_leg_cloak_top = right_leg.addOrReplaceChild("right_leg_cloak_top", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_leg_cloak_top_r1 = right_leg_cloak_top.addOrReplaceChild("right_leg_cloak_top_r1", CubeListBuilder.create().texOffs(56, 50).addBox(-6.0F, 8.0F, -3.0F, 6.0F, 24.0F, 6.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, -0.0087F));
        PartDefinition right_leg_cloak_front = right_leg.addOrReplaceChild("right_leg_cloak_front", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 0.0F));
        PartDefinition right_leg_cloak_front_r1 = right_leg_cloak_front.addOrReplaceChild("right_leg_cloak_front_r1", CubeListBuilder.create().texOffs(32, 102).addBox(-5.8F, -0.5F, -3.2F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.9F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition right_leg_cloak_side = right_leg.addOrReplaceChild("right_leg_cloak_side", CubeListBuilder.create().texOffs(56, 80).addBox(-5.8F, 0.54F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, 18.0F, 0.0F, 0.0873F, 0.0F, 0.096F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(DraculaRenderer.DraculaRenderState renderState) {
        super.setupAnim(renderState);
        this.walkAnimation.applyWalk(renderState.walkAnimationPos, renderState.walkAnimationSpeed, 3,2);
        this.idleAnimation.apply((long) renderState.ageInTicks * 50,  1);
    }

    @Override
    public void translateToHand(DraculaRenderer.DraculaRenderState draculaRenderState, HumanoidArm humanoidArm, PoseStack poseStack) {

    }

    @Override
    public boolean hasArms() {
        return false;
    }
}
