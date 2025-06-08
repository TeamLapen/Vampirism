package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.lib.lib.client.VertexUtils;
import de.teamlapen.vampirism.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.client.core.ModMaterials;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.neoforged.neoforge.client.model.data.ModelData;

public class BloodContainerRenderer implements BlockEntityRenderer<BloodContainerBlockEntity> {

    public BloodContainerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BloodContainerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelData modelData = blockEntity.getModelData();
        var level = modelData.get(BloodContainerBlockEntity.FLUID_LEVEL_PROP);
        if (level != null && level > 0) {
            float filled = Math.clamp(blockEntity.getFluid().getAmount() / (float) BloodContainerBlockEntity.CAPACITY,0f,1f);
            Boolean impure = modelData.get(BloodContainerBlockEntity.FLUID_IMPURE);
            Material material = impure != null && impure ? ModMaterials.IMPURE_BLOOD_MATERIAL : ModMaterials.BLOOD_MATERIAL;
            poseStack.pushPose();
            poseStack.translate(8 / 16f, 1 / 16f, 8 / 16f);
            poseStack.scale(10 / 16f,14 / 16f,10 / 16f);
            VertexConsumer vertex = material.buffer(bufferSource, RenderType::entityTranslucent);
            VertexUtils.addCube(vertex, poseStack, 1, filled, packedLight, packedOverlay, -1, 0.85f);
            poseStack.popPose();
        }
    }
}
