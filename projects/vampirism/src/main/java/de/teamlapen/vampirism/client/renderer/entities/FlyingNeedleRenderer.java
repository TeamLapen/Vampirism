package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.flying_needle.FlyingNeedleModel;
import de.teamlapen.vampirism.common.world.entity.dracula.FlyingNeedleEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class FlyingNeedleRenderer extends EntityRenderer<FlyingNeedleEntity, FlyingNeedleRenderer.FlyingNeedleRenderState> {

    private static final Identifier TEXTURE = VIdentifier.mod("textures/entity/flying_needle/flying_needle.png");
    private final FlyingNeedleModel model;

    public FlyingNeedleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new FlyingNeedleModel(context.bakeLayer(ModEntitiesRender.FLYING_NEEDLE));
    }

    @Override
    public void extractRenderState(FlyingNeedleEntity entity, FlyingNeedleRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getViewYRot(partialTick);
        state.xRot = entity.getViewXRot(partialTick);
    }

    @Override
    public void submit(FlyingNeedleRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - renderState.yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.xRot));
        this.model.setupAnim(renderState);
        nodeCollector.submitModel(this.model, renderState, poseStack, RenderTypes.entityCutout(TEXTURE), renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0,null);
        poseStack.popPose();
    }

    @Override
    public FlyingNeedleRenderState createRenderState() {
        return new FlyingNeedleRenderState();
    }

    public static class FlyingNeedleRenderState extends EntityRenderState {
        public float yRot;
        public float xRot;
    }
}
