package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import de.teamlapen.lib.lib.client.VertexUtils;
import de.teamlapen.vampirism.blockentity.BloodGrinderBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.slf4j.Logger;

public class BloodGrinderBlockEntityRenderer implements BlockEntityRenderer<BloodGrinderBlockEntity> {

    public static final Logger LOGGER = LogUtils.getLogger();

    public BloodGrinderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(BloodGrinderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelData modelData = blockEntity.getModelData();

        FluidStack fluidStack = modelData.get(BloodGrinderBlockEntity.FLUID);
        if (fluidStack != null && !fluidStack.isEmpty()) {
            float filled = Mth.clamp(fluidStack.getAmount() / (float) BloodGrinderBlockEntity.CAPACITY, 0f, 1f);
            IClientFluidTypeExtensions fluidExtension = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            Material material = new Material(TextureAtlas.LOCATION_BLOCKS, fluidExtension.getStillTexture(fluidStack));

            poseStack.pushPose();

            poseStack.translate(8 / 16f, 4 / 16f, 8 / 16f);
            poseStack.scale(15.75f / 16f, 8 / 16f, 15.75f / 16f);

            VertexConsumer vertex = material.buffer(bufferSource, RenderType::entityTranslucent);
            VertexUtils.addCube(vertex, poseStack, 1, filled, packedLight, packedOverlay, -1, 0.85f);

            poseStack.popPose();
        }
    }
}
