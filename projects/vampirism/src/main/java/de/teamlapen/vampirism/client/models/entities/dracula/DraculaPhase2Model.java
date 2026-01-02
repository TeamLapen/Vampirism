package de.teamlapen.vampirism.client.models.entities.dracula;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entities.DraculaRenderer;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;

public class DraculaPhase2Model extends DraculaModel {

    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation needleAttack1;
    private final KeyframeAnimation needleAttack2;
    private final KeyframeAnimation flyingSwordAttack1;
    private final KeyframeAnimation flyingSwordAttack2;

    private final ModelPart body;
    private final ModelPart shirt;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftHand;
    private final ModelPart rightHand;

    public DraculaPhase2Model(ModelPart root) {
        super(root);

        this.body = root.getChild("body");
        this.shirt = this.body.getChild("shirt");
        this.leftArm = this.shirt.getChild("left_arm");
        this.rightArm = this.shirt.getChild("right_arm");
        this.leftHand = this.leftArm.getChild("left_hand");
        this.rightHand = this.rightArm.getChild("right_hand");

        this.walkAnimation = DraculaAnimations.Phase2.WALK.bake(root);
        this.idleAnimation = DraculaAnimations.Phase2.IDLE.bake(root);
        this.needleAttack1 = DraculaAnimations.Phase2.NEEDLES_ATTACK_1.bake(root);
        this.needleAttack2 = DraculaAnimations.Phase2.NEEDLES_ATTACK_2.bake(root);
        this.flyingSwordAttack1 = DraculaAnimations.Phase2.FLYING_SWORD_ATTACK_1.bake(root);
        this.flyingSwordAttack2 = DraculaAnimations.Phase2.FLYING_SWORD_ATTACK_2.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -20.0F, 0.0F, -0.0262F, 0.0F, 0.0F));
        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(120, 16).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, -0.1309F, 0.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(96, 0).addBox(-6.0F, -7.0F, -1.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(2.0F, -5.0F, -3.0F, 0.2618F, 0.0F, 0.0F));
        PartDefinition shirt = body.addOrReplaceChild("shirt", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0175F, 0.0F, 0.0F));
        PartDefinition left_arm = shirt.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 47).addBox(0.0F, 0.0F, -2.77F, 4.0F, 28.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -19.0F, 0.5F, 0.0F, 0.0F, -0.0436F));
        PartDefinition left_hand = left_arm.addOrReplaceChild("left_hand", CubeListBuilder.create(), PartPose.offset(2.0F, 26.0F, -0.5F));
        PartDefinition left_arm_cuff = left_arm.addOrReplaceChild("left_arm_cuff", CubeListBuilder.create().texOffs(80, 56).addBox(0.0F, 0.0F, -2.77F, 4.0F, 28.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = shirt.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(60, 47).addBox(-4.0F, 0.0F, -2.77F, 4.0F, 28.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -19.0F, 0.5F, 0.0F, 0.0F, 0.0436F));
        PartDefinition right_hand = right_arm.addOrReplaceChild("right_hand", CubeListBuilder.create(), PartPose.offset(-2.0F, 26.0F, -0.5F));
        PartDefinition right_arm_cuff = right_arm.addOrReplaceChild("right_arm_cuff", CubeListBuilder.create().texOffs(100, 56).addBox(-4.0F, 0.0F, -2.77F, 4.0F, 28.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shirt_top = shirt.addOrReplaceChild("shirt_top", CubeListBuilder.create().texOffs(92, 24).addBox(-6.0F, 0.0F, -2.27F, 12.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, -0.0349F, 0.0F, 0.0F));
        PartDefinition bow_tie = shirt_top.addOrReplaceChild("bow_tie", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -2.8382F));
        PartDefinition bow_tie_center = bow_tie.addOrReplaceChild("bow_tie_center", CubeListBuilder.create().texOffs(106, 16).addBox(-1.0F, -1.0F, 0.2115F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1571F, 0.0F, 0.0F));
        PartDefinition bow_tie_right = bow_tie.addOrReplaceChild("bow_tie_right", CubeListBuilder.create().texOffs(108, 18).addBox(-0.5F, -1.0F, 0.3382F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1396F, 0.0698F, 0.384F));
        PartDefinition bow_tie_left = bow_tie.addOrReplaceChild("bow_tie_left", CubeListBuilder.create().texOffs(96, 18).addBox(-5.5F, -1.0F, 0.3382F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1396F, -0.0698F, -0.384F));
        PartDefinition shirt_right = shirt.addOrReplaceChild("shirt_right", CubeListBuilder.create().texOffs(104, 38).addBox(-6.0F, 7.0F, -2.5F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 0.0F, 0.0F, -0.0175F));
        PartDefinition shirt_left = shirt.addOrReplaceChild("shirt_left", CubeListBuilder.create().texOffs(80, 38).addBox(0.0F, 7.0F, -2.51F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 0.0F, 0.0F, 0.0175F));
        PartDefinition cloak_collar = body.addOrReplaceChild("cloak_collar", CubeListBuilder.create().texOffs(68, 0).addBox(-6.0F, -9.0F, 1.0F, 12.0F, 10.0F, 0.0F, new CubeDeformation(0.003F)), PartPose.offsetAndRotation(0.0F, -19.0F, 4.0F, -0.1745F, 0.0F, 0.0F));
        PartDefinition cloak_collar_right = cloak_collar.addOrReplaceChild("cloak_collar_right", CubeListBuilder.create().texOffs(80, 4).addBox(-6.0F, -9.0F, -7.0F, 0.0F, 10.0F, 8.0F, new CubeDeformation(0.003F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cloak_collar_left = cloak_collar.addOrReplaceChild("cloak_collar_left", CubeListBuilder.create().texOffs(64, 4).addBox(6.0F, -9.0F, -7.0F, 0.0F, 10.0F, 8.0F, new CubeDeformation(0.003F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cloak = body.addOrReplaceChild("cloak", CubeListBuilder.create(), PartPose.offset(0.0F, -19.0F, 5.0F));
        PartDefinition cloak_center = cloak.addOrReplaceChild("cloak_center", CubeListBuilder.create().texOffs(128, 0).addBox(-8.0F, -1.0F, -1.0F, 16.0F, 62.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1047F, 0.0F, 0.0F));
        PartDefinition cloak_right = cloak.addOrReplaceChild("cloak_right", CubeListBuilder.create().texOffs(192, 50).addBox(-10.0F, 1.0F, -0.9F, 16.0F, 60.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1047F, 0.0349F, 0.2269F));
        PartDefinition cloak_left = cloak.addOrReplaceChild("cloak_left", CubeListBuilder.create().texOffs(160, 50).addBox(-6.0F, 1.0F, -0.9F, 16.0F, 60.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1047F, -0.0349F, -0.2269F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(0.0F, -20.0F, 0.0F));
        PartDefinition left_leg_r1 = left_leg.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(32, 81).addBox(0.0F, -0.2F, -3.0F, 6.0F, 44.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0436F, 0.0F, -0.0087F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(0.0F, -20.0F, 0.0F));
        PartDefinition right_leg_r1 = right_leg.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(56, 81).addBox(-6.0F, -0.2F, -3.0F, 6.0F, 44.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0436F, 0.0F, 0.0087F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(DraculaRenderer.DraculaRenderState renderState) {
        super.setupAnim(renderState);
        this.walkAnimation.applyWalk(renderState.walkAnimationPos, renderState.walkAnimationSpeed, 2,2);
        this.idleAnimation.apply((long) renderState.ageInTicks * 50,  1);

        var keyFrame = switch (renderState.attackAnimationType) {
            case NEEDLE_1 -> this.needleAttack1;
            case NEEDLE_2 -> this.needleAttack2;
            case FLYING_SWORD_1 -> this.flyingSwordAttack1;
            case FLYING_SWORD_2 -> this.flyingSwordAttack2;
            default -> null;
        };
        if (keyFrame != null) {
            keyFrame.apply(renderState.attackAnimation, renderState.ageInTicks);
        }
    }

    @Override
    public void translateToHand(DraculaRenderer.DraculaRenderState renderState, HumanoidArm arm, PoseStack poseStack) {
        root().translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        this.shirt.translateAndRotate(poseStack);
        switch (arm) {
            case LEFT -> {
                this.leftArm.translateAndRotate(poseStack);
                this.leftHand.translateAndRotate(poseStack);
            }
            case RIGHT -> {
                this.rightArm.translateAndRotate(poseStack);
                this.rightHand.translateAndRotate(poseStack);
            }
        }
    }

    @Override
    public boolean hasArms() {
        return true;
    }
}
