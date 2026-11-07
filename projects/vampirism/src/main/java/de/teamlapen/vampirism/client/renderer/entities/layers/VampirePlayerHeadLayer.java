package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.client.renderer.TextureLoader;
import de.teamlapen.vampirism.client.renderer.VampireFaceOverlay;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.properties.Customization;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.SequencedMap;

public class VampirePlayerHeadLayer<T extends AvatarRenderState, Q extends PlayerModel> extends RenderLayer<T, Q> {

    private static final Identifier FALLBACK_EYE = VIdentifier.mod("textures/entity/eyes/eyes0.png");
    private static final Identifier FALLBACK_FANG = VIdentifier.mod("textures/entity/fangs/fangs0.png");

    private static final float SURFACE_GAP = 0.01f;

    private final SequencedMap<Identifier, Identifier> eyeOverlays;
    private final SequencedMap<Identifier, Identifier> fangOverlays;

    public VampirePlayerHeadLayer(RenderLayerParent<T, Q> entityRendererIn) {
        super(entityRendererIn);
        this.eyeOverlays = TextureLoader.mapTexturesInById("textures/entity/eyes");
        this.fangOverlays = TextureLoader.mapTexturesInById("textures/entity/fangs");
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, T renderState, float yRot, float xRot) {
        if (!ModConfig.client().renderVampireEyes.get() || renderState.deathTime > 0) return;
        if (renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_LEVEL, 0) > 0 && !renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_DISGUISE, false) && !renderState.isInvisible) {

            Identifier eyeType = renderState.getRenderData(ModEntityRenderStates.VAMPIRE_EYE_TYPE);
            Identifier fangType = renderState.getRenderData(ModEntityRenderStates.VAMPIRE_FANG_TYPE);
            ModelPart head = this.getParentModel().head;

            if (!Objects.equals(Customization.NONE, eyeType)) {
                Identifier eye = this.eyeOverlays.getOrDefault(eyeType, FALLBACK_EYE);
                boolean glowing = renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_GLOWING_EYES, false);
                RenderType type = glowing ? RenderTypes.eyes(eye) : RenderTypes.entityTranslucentEmissive(eye);
                submitOverlay(poseStack, nodeCollector, head, eye, true, type, renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_EYE_POSITION, Customization.DEFAULT_POSITION), packedLight);
            }
            if (!Objects.equals(Customization.NONE, fangType)) {
                Identifier fang = this.fangOverlays.getOrDefault(fangType, FALLBACK_FANG);
                submitOverlay(poseStack, nodeCollector, head, fang, false, RenderTypes.entityTranslucentEmissive(fang), renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_FANG_POSITION, Customization.DEFAULT_POSITION), packedLight);
            }
        }
    }

    private static void submitOverlay(PoseStack poseStack, SubmitNodeCollector nodeCollector, ModelPart head, Identifier texture, boolean eyes, RenderType type, int position, int packedLight) {
        List<VampireFaceOverlay.Sprite> sprites = position >= 0 ? VampireFaceOverlay.sprites(texture, eyes, position) : null;
        if (sprites == null) {
            nodeCollector.submitModelPart(head, poseStack, type, packedLight, OverlayTexture.NO_OVERLAY, null);
            return;
        }

        poseStack.pushPose();
        head.translateAndRotate(poseStack);
        nodeCollector.submitCustomGeometry(poseStack, type, (pose, vertexBuilder) -> {
            for (VampireFaceOverlay.Sprite sprite : sprites) {
                var source = sprite.source();
                float left = (sprite.x() - 4) / 16f;
                float right = (sprite.x() + source.width() - 4) / 16f;
                float top = (sprite.y() - 8) / 16f;
                float bottom = (sprite.y() + source.height() - 8) / 16f;
                float front = (-4 - SURFACE_GAP) / 16f;
                float u0 = (VampireFaceOverlay.FACE_U + source.x()) / (float) VampireFaceOverlay.TEXTURE_SIZE;
                float u1 = (VampireFaceOverlay.FACE_U + source.x() + source.width()) / (float) VampireFaceOverlay.TEXTURE_SIZE;
                float v0 = (VampireFaceOverlay.FACE_V + source.y()) / (float) VampireFaceOverlay.TEXTURE_SIZE;
                float v1 = (VampireFaceOverlay.FACE_V + source.y() + source.height()) / (float) VampireFaceOverlay.TEXTURE_SIZE;
                vertex(vertexBuilder, pose, left, bottom, front, u0, v1, packedLight);
                vertex(vertexBuilder, pose, right, bottom, front, u1, v1, packedLight);
                vertex(vertexBuilder, pose, right, top, front, u1, v0, packedLight);
                vertex(vertexBuilder, pose, left, top, front, u0, v0, packedLight);
            }
        });
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer vertexBuilder, PoseStack.Pose pose, float x, float y, float z, float u, float v, int packedLight) {
        vertexBuilder.addVertex(pose, x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0, 0, -1);
    }
}
