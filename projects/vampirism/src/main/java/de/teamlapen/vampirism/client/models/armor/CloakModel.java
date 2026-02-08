package de.teamlapen.vampirism.client.models.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.joml.Quaternionf;

public class CloakModel<T extends HumanoidRenderState> extends HumanoidModel<T> {

    private static final String CLOAK = "cloak";

    private final ModelPart cloak;

    public CloakModel(ModelPart root) {
        super(root);
        ModelPart child = root.getChild("body").getChild("jacket");
        this.cloak = child.getChild(CLOAK);
    }

    public static LayerDefinition createCloakLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition holder = jacket.addOrReplaceChild("holder", CubeListBuilder.create().texOffs(1, 0).addBox(-5.0F, -1.0F, -0.02F, 10.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 3.1416F, 0.0F));
        PartDefinition cloak = jacket.addOrReplaceChild(CLOAK, CubeListBuilder.create().texOffs(0, 3).addBox(-5.0F, -0.02F, -1.0F, 10.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 3.1416F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition right_sleeve = right_arm.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition left_sleeve = left_arm.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition right_pants = right_leg.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition left_pants = left_leg.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T state) {
        super.setupAnim(state);

        float capeLean = state instanceof AvatarRenderState playerState ? playerState.capeLean : 0.0F;
        float capeFlap = state instanceof AvatarRenderState playerState ? playerState.capeLean2 : 0.0F;

        this.cloak.rotateBy(new Quaternionf().rotateX(Math.max(4.0F + capeLean / 3.0F + capeFlap, 1.0F) * -(float) Math.PI / 180.0F));
    }
}
