package de.teamlapen.vampirism.client.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import org.jetbrains.annotations.NotNull;

/**
 * Attire designed for the female vampire baroness - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronessAttireModel extends EntityModel<VampireBaronEntity> {
    private static final String VEIL = "veil";
    private static final String DRESS_ARM_LEFT = "dress_arm_left";
    private static final String DRESS_ARM_RIGHT = "dress_arm_right";
    private static final String DRESS_CURTAIN = "dress_curtain";
    private static final String HOOD = "hood";
    private static final String HAT = "hat";
    private static final String HAT2 = "hat2";
    private static final String DRESS_TORSO = "dress_torso";
    private static final String CLOAK = "cloak";

    public final ModelPart dressTorso;
    public final ModelPart dressArmBandRight;
    public final ModelPart dressArmBandLeft;
    public final ModelPart hat;
    public final ModelPart hood;

    public final ModelPart dressCurtain;
    public final ModelPart hat2;
    public final ModelPart veil;
    public final ModelPart cloak;

    private float enragedProgress = 0;

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        PartDefinition hat = part.addOrReplaceChild(HAT, CubeListBuilder.create().texOffs(68, 36).addBox(-3, -8, -3, 6, 1, 6), PartPose.ZERO);
        PartDefinition hood = part.addOrReplaceChild(HOOD, CubeListBuilder.create().texOffs(44, 0).addBox(-4.5f, -8.5f, -4f, 9, 9, 9), PartPose.ZERO);
        PartDefinition dressTorso = part.addOrReplaceChild(DRESS_TORSO, CubeListBuilder.create().texOffs(72, 30).addBox(-4, 0, -2, 8, 12, 4, new CubeDeformation(0.4f)), PartPose.ZERO);
        hat.addOrReplaceChild(VEIL, CubeListBuilder.create().texOffs(32, 28).addBox(-4.5f, -8.5f, -4.5f, 9, 9, 9), PartPose.ZERO);
        part.addOrReplaceChild(DRESS_ARM_LEFT, CubeListBuilder.create().texOffs(60, 46).addBox(0, 2, -2, 3, 3, 5, new CubeDeformation(0.5f)), PartPose.offset(4, 2, 0));
        dressTorso.addOrReplaceChild(DRESS_CURTAIN, CubeListBuilder.create().texOffs(64, 43).addBox(-6, 0, -4, 12, 11, 10), PartPose.offset(0, 12, 0));
        hat.addOrReplaceChild(HAT2, CubeListBuilder.create().texOffs(72, 30).addBox(-2, -11, -2, 4, 2, 4), PartPose.ZERO);
        hood.addOrReplaceChild(CLOAK, CubeListBuilder.create().texOffs(0, 0).addBox(-8.5f, -1, -2.5f, 17, 22, 5), PartPose.rotation(0.3141592653589793F, 0.0F, 0.0F));
        part.addOrReplaceChild(DRESS_ARM_RIGHT, CubeListBuilder.create().texOffs(60, 46).addBox(-3, 2, -2, 3, 3, 4, new CubeDeformation(0.5f)), PartPose.offset(-4, 2, 0));
        return LayerDefinition.create(mesh, 128, 64);
    }

    public BaronessAttireModel(ModelPart part) {
        dressTorso = part.getChild(DRESS_TORSO);
        dressArmBandLeft = part.getChild(DRESS_ARM_LEFT);
        dressArmBandRight = part.getChild(DRESS_ARM_RIGHT);
        hat = part.getChild(HAT);
        hood = part.getChild(HOOD);
        dressCurtain = dressTorso.getChild(DRESS_CURTAIN);
        hat2 = hat.getChild(HAT2);
        veil = hat.getChild(VEIL);
        cloak = hood.getChild(CLOAK);

    }

    @Override
    public void prepareMobModel(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        enragedProgress = entityIn.getEnragedProgress();
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack matrixStackIn, @NotNull VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.dressArmBandLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.dressArmBandRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.dressTorso.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.hat.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pushPose();
        matrixStackIn.scale(1 - 0.5f * enragedProgress, 1 - 0.7f * enragedProgress, 1 - 0.5f * enragedProgress);
        this.hood.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelPart renderer, float x, float y, float z) {
        renderer.xRot = x;
        renderer.yRot = y;
        renderer.zRot = z;
    }

    @Override
    public void setupAnim(@NotNull VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float bodyRotateY = 0;
        float headRotateY = 0;
        headRotateY = netHeadYaw * ((float) Math.PI / 180f);
        if (this.attackTime > 0.0F) {
            HumanoidArm handside = this.getSwingingSide(entityIn);
            float f1 = this.attackTime;
            bodyRotateY = Mth.sin(Mth.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HumanoidArm.LEFT) {
                bodyRotateY *= -1.0F;
            }
        }

        this.hat.yRot = bodyRotateY + headRotateY;
        this.veil.yRot = bodyRotateY + headRotateY;
        this.hood.yRot = bodyRotateY + headRotateY;
        this.hat2.yRot = bodyRotateY + headRotateY;
        this.cloak.yRot = bodyRotateY;
        this.dressCurtain.yRot = bodyRotateY;
        this.dressTorso.yRot = bodyRotateY;
        this.dressArmBandLeft.yRot = bodyRotateY;
        this.dressArmBandRight.yRot = bodyRotateY;
    }

    protected HumanoidArm getSwingingSide(VampireBaronEntity entity) {
        HumanoidArm handside = entity.getMainArm();
        return entity.swingingArm == InteractionHand.MAIN_HAND ? handside : handside.getOpposite();
    }
}