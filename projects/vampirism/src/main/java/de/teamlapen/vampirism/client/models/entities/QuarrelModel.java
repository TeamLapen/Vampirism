package de.teamlapen.vampirism.client.models.entities;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public class QuarrelModel extends EntityModel<ArrowRenderState> {

    public QuarrelModel(ModelPart root) {
        super(root, RenderTypes::entityCutoutCull);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        PartDefinition quarrel = part.addOrReplaceChild("quarrel", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.4F, -20.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        quarrel.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 0).addBox(3.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.5F, 7.0F, -2.3562F, 1.5708F, 0.0F));
        quarrel.addOrReplaceChild("cross_1", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -1.5F, 0.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.5F, -4.0F, -2.3562F, 1.5708F, 0.0F));
        quarrel.addOrReplaceChild("cross_2", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -1.5F, 0.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.5F, -4.0F, -0.7854F, 1.5708F, 0.0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(ArrowRenderState state) {
        super.setupAnim(state);

        if (state.shake > 0.0F) {
            float pow = -Mth.sin(state.shake * 3.0F) * state.shake;
            this.root.zRot += pow * ((float)Math.PI / 180F);
        }
    }
}
