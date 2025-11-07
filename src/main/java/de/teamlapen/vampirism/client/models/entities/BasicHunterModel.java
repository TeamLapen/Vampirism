package de.teamlapen.vampirism.client.models.entities;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.NotNull;

/**
 * Model for Basic Vampire Hunter
 */
public class BasicHunterModel<T extends AvatarRenderState> extends BipedCloakedModel<T> {

    public static @NotNull LayerDefinition createBodyLayer() {
        return LayerDefinition.create(BipedCloakedModel.createMesh(false), 64, 64);
    }

    public static @NotNull LayerDefinition createSlimBodyLayer() {
        return LayerDefinition.create(BipedCloakedModel.createMesh(true), 64, 64);
    }

    public BasicHunterModel(ModelPart part, boolean smallArms) {
        super(part, smallArms);
    }

}
