package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;


public class MinionModel<T extends MinionEntity>  extends PlayerModel<T> {
    public MinionModel(float modelSize) {
        super(modelSize, false);
    }

    @Override
    public Iterable<ModelRenderer> getBodyParts() {
        return super.getBodyParts();
    }
}
