package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.entity.dracula.FlyingSwordEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public class FlyingSwordRenderer extends EntityRenderer<FlyingSwordEntity, FlyingSwordRenderer.FlyingSwordRenderState> {
    private static final Identifier TEXTURE = VIdentifier.mod("textures/entity/dracula/flying_sword.png");

    public FlyingSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public FlyingSwordRenderState createRenderState() {
        return new FlyingSwordRenderState();
    }

    @Override
    public void extractRenderState(FlyingSwordEntity entity, FlyingSwordRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getViewYRot(partialTick);
        state.xRot = entity.getViewXRot(partialTick);
    }

    @Override
    public void submit(FlyingSwordRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(- state.yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.xRot));

        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, vertexBuilder) -> {
            float size = 1f;
            Matrix4f matrix4f = pose.pose();
            int light = 15728880;

            // Render on the right side (looking from behind)
            // U=0 (arrow tip) should be at Z = +size (forward)
            // U=1 (arrow tail) should be at Z = -size (backward)
            vertex0(vertexBuilder, matrix4f, pose, light, 0.01f, -size, -size, 1, 1, 1, 0, 0);
            vertex0(vertexBuilder, matrix4f, pose, light, 0.01f, -size,  size, 0, 1, 1, 0, 0);
            vertex0(vertexBuilder, matrix4f, pose, light, 0.01f,  size,  size, 0, 0, 1, 0, 0);
            vertex0(vertexBuilder, matrix4f, pose, light, 0.01f,  size, -size, 1, 0, 1, 0, 0);

            // Render on the left side (looking from behind)
            // U=0 (arrow tip) should be at Z = +size (forward)
            // U=1 (arrow tail) should be at Z = -size (backward)
            // Normal is pointing left (-1, 0, 0).
            // Winding order reversed to keep it counter-clockwise for the outside.
            vertex0(vertexBuilder, matrix4f, pose, light, -0.01f, -size,  size, 0, 1, -1, 0, 0);
            vertex0(vertexBuilder, matrix4f, pose, light, -0.01f, -size, -size, 1, 1, -1, 0, 0);
            vertex0(vertexBuilder, matrix4f, pose, light, -0.01f,  size, -size, 1, 0, -1, 0, 0);
            vertex0(vertexBuilder, matrix4f, pose, light, -0.01f,  size,  size, 0, 0, -1, 0, 0);
        });

        poseStack.popPose();
        super.submit(state, poseStack, nodeCollector, cameraRenderState);
    }

    private static void vertex0(VertexConsumer pConsumer, Matrix4f pMatrix, PoseStack.Pose pPose, int pLight, float pX, float pY, float pZ, float pU, float pV, float nX, float nY, float nZ) {
        pConsumer.addVertex(pMatrix, pX, pY, pZ)
                .setColor(255, 255, 255, 255)
                .setUv(pU, pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(pLight)
                .setNormal(pPose, nX, nY, nZ);
    }

    public static class FlyingSwordRenderState extends EntityRenderState {
        public float yRot;
        public float xRot;
    }
}
