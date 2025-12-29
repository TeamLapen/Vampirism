package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.common.world.blockentity.PedestalBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity, PedestalRenderer.PedestalBlockEntityRenderer> {

    private final ItemModelResolver itemModelResolver;

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void extractRenderState(PedestalBlockEntity blockEntity, PedestalBlockEntityRenderer renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        this.itemModelResolver.updateForTopItem(renderState.itemState, blockEntity.getStackForRender(), ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
        renderState.rotation = (blockEntity.getTickForRender() % 512 + partialTick) / 512f;
    }

    @Override
    public PedestalBlockEntityRenderer createRenderState() {
        return new PedestalBlockEntityRenderer();
    }

    @Override
    public void submit(PedestalBlockEntityRenderer renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (!renderState.itemState.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.8, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.rotation * 360));
            renderState.itemState.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class PedestalBlockEntityRenderer extends BlockEntityRenderState {
        public ItemStackRenderState itemState = new ItemStackRenderState();
        public float rotation;
    }
}
