package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.GhostModel;
import de.teamlapen.vampirism.client.renderer.entities.GhostRenderer;
import de.teamlapen.vampirism.common.blockentity.MotherTrophyBlockEntity;
import de.teamlapen.vampirism.common.blocks.MotherTrophyBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MotherTrophyRenderer implements BlockEntityRenderer<MotherTrophyBlockEntity, MotherTrophyRenderer.MotherTrophyRenderState> {

    private final GhostModel model;

    public MotherTrophyRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new GhostModel(context.bakeLayer(ModEntitiesRender.GHOST));
    }

    @Override
    public void extractRenderState(MotherTrophyBlockEntity blockEntity, MotherTrophyRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.rotation = blockEntity.getBlockState().getValue(MotherTrophyBlock.ROTATION);
        renderState.gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
    }

    @Override
    public void submit(MotherTrophyRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0.0F, -1.701F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.rotation));
        this.model.setupAnim2(renderState.gameTime);
        poseStack.translate(0,0.75,0);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        nodeCollector.submitModel(model, renderState.ghostRenderState, poseStack, RenderType.itemEntityTranslucentCull(GhostRenderer.TEXTURE), renderState.lightCoords, OverlayTexture.NO_OVERLAY, -1, null);
        poseStack.popPose();
    }

    @Override
    public MotherTrophyRenderState createRenderState() {
        return new MotherTrophyRenderState();
    }

    public static class MotherTrophyRenderState extends BlockEntityRenderState {
        public float rotation;
        public long gameTime;
        public GhostRenderer.GhostRenderState ghostRenderState = new GhostRenderer.GhostRenderState();
    }
}
