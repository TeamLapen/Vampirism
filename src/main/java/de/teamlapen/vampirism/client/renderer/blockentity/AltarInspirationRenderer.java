package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.client.renderer.VertexUtils;
import de.teamlapen.vampirism.common.blockentity.AltarInspirationBlockEntity;
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

public class AltarInspirationRenderer implements BlockEntityRenderer<AltarInspirationBlockEntity, AltarInspirationRenderer.AltarInspirationRenderState> {

    public AltarInspirationRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public AltarInspirationRenderState createRenderState() {
        return new AltarInspirationRenderState();
    }

    @Override
    public void extractRenderState(AltarInspirationBlockEntity blockEntity, AltarInspirationRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.stack = blockEntity.getFluid();
    }

    @Override
    public void submit(AltarInspirationRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        VertexUtils.renderFluidTank(
                renderState.stack,
                AltarInspirationBlockEntity.CAPACITY,
                new Vec3(8 / 16f, 1 / 16f, 8 / 16f),
                new Vec3(8 / 16f,10.8 / 16f,8 / 16f),
                0.85f,
                poseStack,
                nodeCollector,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY
        );
    }

    public static class AltarInspirationRenderState extends BlockEntityRenderState {
        public FluidStack stack = FluidStack.EMPTY;
    }
}
