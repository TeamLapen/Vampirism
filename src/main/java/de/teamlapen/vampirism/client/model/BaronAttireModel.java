package de.teamlapen.vampirism.client.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

/**
 * Attire designed for the male vampire baron - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronAttireModel extends EntityModel<VampireBaronEntity> {

    private static final String HOOD = "hood";
    private static final String CLOAK = "cloak";

    public final @NotNull ModelPart hood;
    public final @NotNull ModelPart cloak;
    private float enragedProgress = 0;

    public static @NotNull LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        PartDefinition hood = part.addOrReplaceChild(HOOD, CubeListBuilder.create().texOffs(44, 0).addBox(-4.5f, -8.5f, -4, 9, 9, 9), PartPose.ZERO);
        hood.addOrReplaceChild(CLOAK, CubeListBuilder.create().texOffs(0, 0).addBox(-8.5f, -0.5f, -2.5f, 17, 22, 5), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    public BaronAttireModel(@NotNull ModelPart part) {
        hood = part.getChild(HOOD);
        cloak = hood.getChild(CLOAK);
    }

    @Override
    public void prepareMobModel(@NotNull VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        enragedProgress = entityIn.getEnragedProgress();
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack matrixStackIn, @NotNull VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        hood.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pushPose();
        matrixStackIn.scale(1 - 0.4f * enragedProgress, 1 - 0.7f * enragedProgress, 1 - 0.4f * enragedProgress);
        cloak.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }

    @Override
    public void setupAnim(@NotNull VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float bodyRotateAngleY = 0;
        if (this.attackTime > 0.0F) {
            HumanoidArm handside = this.getSwingingSide(entityIn);
            float f1 = this.attackTime;
            bodyRotateAngleY = Mth.sin(Mth.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HumanoidArm.LEFT) {
                bodyRotateAngleY *= -1.0F;
            }
        }
        this.hood.yRot = bodyRotateAngleY;
        this.cloak.yRot = bodyRotateAngleY;
    }


    protected @NotNull HumanoidArm getSwingingSide(@NotNull VampireBaronEntity entity) {
        HumanoidArm handside = entity.getMainArm();
        return entity.swingingArm == InteractionHand.MAIN_HAND ? handside : handside.getOpposite();
    }
}