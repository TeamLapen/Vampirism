package de.teamlapen.vampirism.client.models.armor;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;


public class VampirismArmorModel extends Model<Object> {

    public VampirismArmorModel(ModelPart root) {
        super(root, RenderTypes::entityCutoutNoCull);
    }


    protected @NotNull Iterable<ModelPart> getBodyModels() {
        return Collections.emptyList();
    }

    protected @NotNull Iterable<ModelPart> getHeadModels() {
        return Collections.emptyList();
    }

    protected @NotNull Iterable<ModelPart> getLeftLegModels() {
        return Collections.emptyList();
    }

    protected @NotNull Iterable<ModelPart> getRightLegModels() {
        return Collections.emptyList();
    }

    protected @NotNull Iterable<ModelPart> getLeftArmModels() {
        return Collections.emptyList();
    }

    protected @NotNull Iterable<ModelPart> getRightArmModels() {
        return Collections.emptyList();
    }

    public void setupAnim(HumanoidRenderState state) {
    }
}
