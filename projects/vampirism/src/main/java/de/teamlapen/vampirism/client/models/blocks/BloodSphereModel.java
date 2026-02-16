package de.teamlapen.vampirism.client.models.blocks;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class BloodSphereModel extends Model<Object> {

    private final ModelPart sphere;

    public BloodSphereModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.sphere = root.getChild("sphere");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("sphere", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    public void setupAnim(float rotationY, float bobbingOffset) {
        this.sphere.yRot = rotationY;
        this.sphere.y = bobbingOffset;
    }

    public ModelPart getSphere() {
        return this.sphere;
    }
}
