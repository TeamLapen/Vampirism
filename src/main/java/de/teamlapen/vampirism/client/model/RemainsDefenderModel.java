package de.teamlapen.vampirism.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.entity.RemainsDefenderEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class RemainsDefenderModel extends EntityModel<RemainsDefenderEntity> {

    private static final String PART1 = "part1";
    private static final String PART2 = "part2";

    protected final @NotNull ModelPart part1;
    protected final @NotNull ModelPart part2;

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition cube = partdefinition.addOrReplaceChild(PART1, CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.01F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        PartDefinition cube2 = partdefinition.addOrReplaceChild(PART2, CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.01F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 24, 24);
    }

    public RemainsDefenderModel(ModelPart part) {
        this.part1 = part.getChild(PART1);
        this.part2 = part.getChild(PART2);
    }

    @Override
    public void setupAnim(RemainsDefenderEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int color) {
        this.part1.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, color);
        this.part2.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, color);
    }
}
