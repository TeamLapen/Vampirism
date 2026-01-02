package de.teamlapen.vampirism.client.models.entities.dracula;

import de.teamlapen.vampirism.client.renderer.entities.DraculaRenderer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class DraculaModel extends EntityModel<DraculaRenderer.DraculaRenderState> implements ArmedModel<DraculaRenderer.DraculaRenderState> {

    protected DraculaModel(ModelPart root) {
        super(root);
    }

    public abstract boolean hasArms();
}
