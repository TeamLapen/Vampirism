package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.GhostModel;
import de.teamlapen.vampirism.client.renderer.entities.GhostRenderer;
import de.teamlapen.vampirism.common.world.blockentity.MotherTrophyBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MotherTrophyRenderer implements BlockEntityRenderer<MotherTrophyBlockEntity, MotherTrophyRenderer.MotherTrophyRenderState> {

    private static final double BOB_SPEED = 0.05;
    private static final float BOB_AMPLITUDE = 0.1f;

    private final GhostModel model;

    public MotherTrophyRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new GhostModel(context.bakeLayer(ModEntitiesRender.GHOST));
    }

    @Override
    public void extractRenderState(MotherTrophyBlockEntity blockEntity, MotherTrophyRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        Level level = blockEntity.getLevel();
        long gameTime = level != null ? level.getGameTime() : 0;
        renderState.gameTime = gameTime;
        renderState.bobOffset = (float) (Math.sin((gameTime + partialTick) * BOB_SPEED) * BOB_AMPLITUDE);
        renderState.playerYaw = Mth.rotLerp(partialTick, blockEntity.prevYaw, blockEntity.currentYaw);
    }

    @Override
    public void submit(MotherTrophyRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0.0F, -1.8F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.playerYaw + 180));
        this.model.setupAnim2(renderState.gameTime);
        poseStack.translate(0, 0.75 - renderState.bobOffset, 0);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        nodeCollector.submitModel(model, renderState.ghostRenderState, poseStack, RenderTypes.entityTranslucentCullItemTarget(GhostRenderer.TEXTURE), renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0, null);
        poseStack.popPose();
    }

    @Override
    public MotherTrophyRenderState createRenderState() {
        return new MotherTrophyRenderState();
    }

    public static class MotherTrophyRenderState extends BlockEntityRenderState {
        public long gameTime;
        public float bobOffset;
        public float playerYaw;
        public GhostRenderer.GhostRenderState ghostRenderState = new GhostRenderer.GhostRenderState();
    }
}
