package de.teamlapen.vampirism.client.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;


public class VampirismArmorModel extends Model {

    public VampirismArmorModel() {
        super(RenderType::entityCutoutNoCull);
    }

    public void copyFromHumanoid(@NotNull HumanoidModel<?> wearerModel) {
        getBodyModels().forEach(p -> p.copyFrom(wearerModel.body));
        getHeadModels().forEach(p -> p.copyFrom(wearerModel.head));
        getRightLegModels().forEach(p -> p.copyFrom(wearerModel.rightLeg));
        getLeftLegModels().forEach(p -> p.copyFrom(wearerModel.leftLeg));
        getRightArmModels().forEach(p -> p.copyFrom(wearerModel.rightArm));
        getLeftArmModels().forEach(p -> p.copyFrom(wearerModel.leftArm));
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.getBodyModels().forEach((modelPart) -> modelPart.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
        this.getHeadModels().forEach((modelPart) -> modelPart.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
        this.getLeftLegModels().forEach((modelPart) -> modelPart.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
        this.getRightLegModels().forEach((modelPart) -> modelPart.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
        this.getLeftArmModels().forEach((modelPart) -> modelPart.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
        this.getRightArmModels().forEach((modelPart) -> modelPart.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
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
}
