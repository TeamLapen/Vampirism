package de.teamlapen.vampirism.client.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

import java.util.Collections;


public class VampirismArmorModel extends Model {

    public VampirismArmorModel() {
        super(RenderType::entityCutoutNoCull);
    }

    public void copyFromHumanoid(HumanoidModel<?> wearerModel) {
        getBodyModels().forEach(p -> p.copyFrom(wearerModel.body));
        getHeadModels().forEach(p -> p.copyFrom(wearerModel.head));
        getRightLegModels().forEach(p -> p.copyFrom(wearerModel.rightLeg));
        getLeftLegModels().forEach(p -> p.copyFrom(wearerModel.leftLeg));
        getRightArmModels().forEach(p -> p.copyFrom(wearerModel.rightArm));
        getLeftArmModels().forEach(p -> p.copyFrom(wearerModel.leftArm));
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.getBodyModels().forEach((p_102061_) -> {
            p_102061_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        });
        this.getHeadModels().forEach((p_102051_) -> {
            p_102051_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        });
        this.getLeftLegModels().forEach((p_102051_) -> {
            p_102051_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        });
        this.getRightLegModels().forEach((p_102051_) -> {
            p_102051_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        });
        this.getLeftArmModels().forEach((p_102051_) -> {
            p_102051_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        });
        this.getRightArmModels().forEach((p_102051_) -> {
            p_102051_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        });
    }

    protected Iterable<ModelPart> getBodyModels() {
        return Collections.emptyList();
    }

    protected Iterable<ModelPart> getHeadModels() {
        return Collections.emptyList();
    }

    protected Iterable<ModelPart> getLeftLegModels() {
        return Collections.emptyList();
    }

    protected Iterable<ModelPart> getRightLegModels() {
        return Collections.emptyList();
    }

    protected Iterable<ModelPart> getLeftArmModels() {
        return Collections.emptyList();
    }

    protected Iterable<ModelPart> getRightArmModels() {
        return Collections.emptyList();
    }
}
