package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.common.blockentity.diffuser.FogDiffuserBlockEntity;
import de.teamlapen.vampirism.common.core.ModItems;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FogDiffuserRenderer implements BlockEntityRenderer<FogDiffuserBlockEntity, FogDiffuserRenderer.FogDiffuserRenderState> {

    private final ItemModelResolver itemModelResolver;
    private final ItemStack motherCore;

    public FogDiffuserRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.motherCore = ModItems.MOTHER_CORE.get().getDefaultInstance();
    }

    @Override
    public void extractRenderState(FogDiffuserBlockEntity blockEntity, FogDiffuserRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        this.itemModelResolver.updateForTopItem(renderState.itemState, motherCore, ItemDisplayContext.GROUND, null, null, 0);
        renderState.degrees = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() + partialTick : 0;
    }

    @Override
    public void submit(FogDiffuserRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.3, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.degrees));
        renderState.itemState.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
    }

    @Override
    public FogDiffuserRenderState createRenderState() {
        return new FogDiffuserRenderState();
    }

    public static class FogDiffuserRenderState extends BlockEntityRenderState {
        public ItemStackRenderState itemState = new ItemStackRenderState();
        private float degrees = 0;
    }
}
