package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.VertexUtils;
import de.teamlapen.vampirism.common.blockentity.BloodGrinderBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

public class BloodGrinderRenderer implements BlockEntityRenderer<BloodGrinderBlockEntity, BloodGrinderRenderer.BloodGrinderRenderState> {

    public BloodGrinderRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BloodGrinderRenderState createRenderState() {
        return new BloodGrinderRenderState();
    }

    @Override
    public void submit(BloodGrinderRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        VertexUtils.renderFluidTank(
                Minecraft.getInstance().level, renderState.blockPos,
                renderState.stack,
                BloodGrinderBlockEntity.CAPACITY,
                new Vec3(0, 0, 0),
                new Vec3(15.75f / 16f, 8 / 16f, 15.75f / 16f),
                poseStack,
                nodeCollector,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY
        );
    }

    @Override
    public void extractRenderState(BloodGrinderBlockEntity blockEntity, BloodGrinderRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        FluidResource resource = blockEntity.fluidInventory.getResource();
        renderState.stack = resource.toStack(blockEntity.fluidInventory.getAmount());
    }

    public static class BloodGrinderRenderState extends BlockEntityRenderState {
        public FluidStack stack;
    }
}
