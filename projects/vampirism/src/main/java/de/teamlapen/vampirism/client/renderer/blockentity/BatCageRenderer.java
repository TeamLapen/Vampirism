package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.world.blockentity.BatCageBlockEntity;
import de.teamlapen.vampirism.common.world.blocks.BatCageBlock;
import net.minecraft.client.model.ambient.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.BatRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BatCageRenderer implements BlockEntityRenderer<BatCageBlockEntity, BatCageRenderer.BatCageRenderState> {

    public static final Identifier BAT_LOCATION = VResourceLocation.mc("textures/entity/bat.png");

    private final BatModel model;

    public BatCageRenderer(BlockEntityRendererProvider.Context context) {
        model = new BatModel(context.bakeLayer(ModelLayers.BAT));
    }

    @Override
    public void extractRenderState(BatCageBlockEntity blockEntity, BatCageRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.containsBat = blockEntity.getBlockState().getValue(BatCageBlock.CONTAINS_BAT);
    }

    @Override
    public BatCageRenderState createRenderState() {
        return new BatCageRenderState();
    }

    @Override
    public void submit(BatCageRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.containsBat) {
            renderBat(poseStack, nodeCollector, renderState.lightCoords);
        }
    }

    private void renderBat(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1F, 0.5F);
        //pPoseStack.mulPose(Axis.YN.rotationDegrees(90 * direction.get2DDataValue()));
        poseStack.scale(0.65F, 0.65F, 0.65F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        BatRenderState batRenderState = new BatRenderState();
        batRenderState.isResting = true;
        batRenderState.restAnimationState.animateWhen(true, 0);
        this.model.setupAnim(batRenderState);
        nodeCollector.submitModel(this.model, batRenderState, poseStack, this.model.renderType(BAT_LOCATION), packedLight, OverlayTexture.NO_OVERLAY, 0, null);
        poseStack.popPose();
    }

    public static class BatCageRenderState extends BlockEntityRenderState {
        public boolean containsBat;
    }
}
