package de.teamlapen.vampirism.client.models.entities;

import de.teamlapen.vampirism.client.renderer.entities.VampireBaronRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class BaronBaseModel extends EntityModel<VampireBaronRenderer.VampireBaronRenderState> {

    public BaronBaseModel(ModelPart root) {
        super(root);
    }

    public abstract ModelPart getBody();

    protected static void copyModelPartProperties(ModelPart original, ModelPart replacement) {
        replacement.visible = original.visible;
        replacement.x = original.x;
        replacement.y = original.y;
        replacement.z = original.z;
        replacement.xRot = original.xRot;
        replacement.yRot = original.yRot;
        replacement.zRot = original.zRot;
        replacement.xScale = original.xScale;
        replacement.yScale = original.yScale;
        replacement.zScale = original.zScale;
    }
}
