package de.teamlapen.vampirism.client.models.entities;

import de.teamlapen.vampirism.client.renderer.entities.VampireBaronRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class BaronBaseModel extends EntityModel<VampireBaronRenderer.VampireBaronRenderState> {

    public BaronBaseModel(ModelPart root) {
        super(root);
    }

    public abstract ModelPart getBody();
}
