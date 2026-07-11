package de.teamlapen.vampirism.client.models.entities.dracula;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entities.DraculaRenderer;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;

public class DraculaPhase3Model extends DraculaModel {

    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation attack1;
    private final KeyframeAnimation attack2;
    private final KeyframeAnimation transformationAnimation;
    private final KeyframeAnimation bloodSiphonAnimation;

    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public DraculaPhase3Model(ModelPart root) {
        super(root);

        ModelPart body = root.getChild("body");
        this.leftArm = body.getChild("left_arm");
        this.rightArm = body.getChild("right_arm");


        this.walkAnimation = DraculaAnimations.Phase3.WALK.bake(root);
        this.idleAnimation = DraculaAnimations.Phase3.IDLE.bake(root);
        this.attack1 = DraculaAnimations.Phase3.ATTACK_1.bake(root);
        this.attack2 = DraculaAnimations.Phase3.ATTACK_2.bake(root);
        this.transformationAnimation = DraculaAnimations.Phase3.TRANSFORMATION.bake(root);
        this.bloodSiphonAnimation = DraculaAnimations.Phase3.BLOOD_SIPHON.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -20.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(120, 16).addBox(-1.0F, -5.0F, -0.5F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, -0.1309F, 0.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, -6.5F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -5.5F, 0.0F, -0.3054F, 0.0F, 0.0F));
        PartDefinition wings = body.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, 4.0F));
        PartDefinition wings_left = wings.addOrReplaceChild("wings_left", CubeListBuilder.create().texOffs(128, 31).mirror().addBox(3.2F, -6.0F, -0.5F, 64.0F, 30.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.258F, -0.5333F, -0.4824F));
        PartDefinition wings_right = wings.addOrReplaceChild("wings_right", CubeListBuilder.create().texOffs(128, 1).addBox(-67.2F, -6.0F, -0.5F, 64.0F, 30.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.258F, 0.5333F, 0.4824F));
        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 43).addBox(0.0F, 0.0F, -2.77F, 4.0F, 28.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -19.0F, 0.5F, -0.0349F, 0.0F, -0.0436F));
        PartDefinition left_arm_cuff = left_arm.addOrReplaceChild("left_arm_cuff", CubeListBuilder.create().texOffs(80, 52).addBox(0.0F, 0.0F, -2.77F, 4.0F, 28.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(60, 43).addBox(-4.0F, 0.0F, -2.77F, 4.0F, 28.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -19.0F, 0.5F, -0.0349F, 0.0F, 0.0436F));
        PartDefinition right_arm_cuff = right_arm.addOrReplaceChild("right_arm_cuff", CubeListBuilder.create().texOffs(100, 52).addBox(-4.0F, 0.0F, -2.77F, 4.0F, 28.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shirt = body.addOrReplaceChild("shirt", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shirt_top = shirt.addOrReplaceChild("shirt_top", CubeListBuilder.create().texOffs(90, 20).addBox(-6.0F, 0.0F, -2.27F, 12.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, -0.0349F, 0.0F, 0.0F));
        PartDefinition shirt_top_extra = shirt_top.addOrReplaceChild("shirt_top_extra", CubeListBuilder.create().texOffs(128, 96).addBox(-6.0F, 0.0F, -2.27F, 12.0F, 8.0F, 6.0F, new CubeDeformation(0.04F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shirt_right = shirt.addOrReplaceChild("shirt_right", CubeListBuilder.create().texOffs(104, 34).addBox(-6.0F, 7.0F, -2.5F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 0.0F, 0.0F, -0.0175F));
        PartDefinition shirt_right_extra = shirt_right.addOrReplaceChild("shirt_right_extra", CubeListBuilder.create().texOffs(152, 110).addBox(-6.0F, 7.0F, -2.5F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.04F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shirt_left = shirt.addOrReplaceChild("shirt_left", CubeListBuilder.create().texOffs(80, 34).addBox(0.0F, 7.0F, -2.51F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 0.0F, 0.0F, 0.0175F));
        PartDefinition shirt_left_extra = shirt_left.addOrReplaceChild("shirt_left_extra", CubeListBuilder.create().texOffs(128, 110).addBox(0.0F, 7.0F, -2.5F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.04F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 77).addBox(0.0F, -0.2F, -3.0F, 6.0F, 44.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.0F, 0.0F, 0.0F, -0.0017F, -0.0436F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(56, 77).addBox(-6.0F, -0.2F, -3.0F, 6.0F, 44.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.0F, 0.0F, 0.0F, 0.0017F, 0.0436F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(DraculaRenderer.DraculaRenderState renderState) {
        super.setupAnim(renderState);
        if (renderState.draculaState.isTransforming) {
            this.transformationAnimation.apply(renderState.transformationAnimation, renderState.ageInTicks);
            return;
        }
        this.walkAnimation.applyWalk(renderState.walkAnimationPos, renderState.walkAnimationSpeed, 1,1);
        this.idleAnimation.apply((long) renderState.ageInTicks * 50,  1);

        var keyFrame = switch (renderState.attackAnimationType) {
            case ATTACK_1 -> this.attack1;
            case ATTACK_2 -> this.attack2;
            case BLOOD_SIPHON -> this.bloodSiphonAnimation;
            default -> null;
        };
        if (keyFrame != null) {
            keyFrame.apply(renderState.attackAnimation, renderState.ageInTicks);
        }
    }

    @Override
    public void translateToHand(DraculaRenderer.DraculaRenderState renderState, HumanoidArm arm, PoseStack poseStack) {
        this.root().translateAndRotate(poseStack);

    }

    @Override
    public boolean hasArms() {
        return true;
    }
}
