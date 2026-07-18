package de.teamlapen.vampirism.client.models.entities.flying_needle;

import de.teamlapen.vampirism.client.renderer.entities.FlyingNeedleRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class FlyingNeedleModel extends EntityModel<FlyingNeedleRenderer.FlyingNeedleRenderState> {

    public FlyingNeedleModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(-4, 4).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition sides = root.addOrReplaceChild("sides", CubeListBuilder.create().texOffs(0, -16).addBox(1.0F, -2.0F, -8.0F, 0.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, -16).addBox(-1.0F, -2.0F, -8.0F, 0.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(-16, 4).addBox(-2.0F, -1.0F, -8.0F, 4.0F, 0.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(-16, 4).addBox(-2.0F, 1.0F, -8.0F, 4.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}
