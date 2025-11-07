package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.client.renderer.VertexUtils;
import de.teamlapen.vampirism.common.blockentity.BloodContainerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class BloodContainerRenderer implements BlockEntityRenderer<BloodContainerBlockEntity, BloodContainerRenderer.BloodContainerRenderState> {

    public BloodContainerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BloodContainerRenderState createRenderState() {
        return new BloodContainerRenderState();
    }

    @Override
    public void extractRenderState(BloodContainerBlockEntity blockEntity, BloodContainerRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.stack = blockEntity.getFluid();
    }

    @Override
    public void submit(BloodContainerRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        VertexUtils.renderFluidTank(
                renderState.stack,
                BloodContainerBlockEntity.CAPACITY,
                new Vec3(8 / 16f, 1 / 16f, 8 / 16f),
                new Vec3(10 / 16f,13.8 / 16f,10 / 16f),
                0.85f,
                poseStack,
                nodeCollector,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY
        );
    }

    public static class BloodContainerRenderState extends BlockEntityRenderState {
        public FluidStack stack = FluidStack.EMPTY;
    }
}
