package de.teamlapen.vampirism.client.model.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;


public class VampirismArmorModel extends Model {

    public VampirismArmorModel(ModelPart root) {
        super(root, RenderType::entityCutoutNoCull);
    }

    public void copyFromHumanoid(@NotNull HumanoidModel<?> wearerModel) {
        getBodyModels().forEach(p -> p.copyFrom(wearerModel.body));
        getHeadModels().forEach(p -> p.copyFrom(wearerModel.head));
        getRightLegModels().forEach(p -> p.copyFrom(wearerModel.rightLeg));
        getLeftLegModels().forEach(p -> p.copyFrom(wearerModel.leftLeg));
        getRightArmModels().forEach(p -> p.copyFrom(wearerModel.rightArm));
        getLeftArmModels().forEach(p -> p.copyFrom(wearerModel.leftArm));
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
