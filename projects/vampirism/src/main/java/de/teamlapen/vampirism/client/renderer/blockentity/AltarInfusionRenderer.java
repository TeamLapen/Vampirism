package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.blocks.BloodSphereModel;
import de.teamlapen.vampirism.common.world.blockentity.AltarInfusionBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;

public class AltarInfusionRenderer implements BlockEntityRenderer<AltarInfusionBlockEntity, AltarInfusionRenderer.AltarInfusionRenderState> {

    private static final Identifier SPHERE_TEXTURE = VIdentifier.mod("textures/entity/blood_sphere.png");
    private static final Identifier INFUSION_BEAM_LOCATION = VIdentifier.mod("textures/entity/infusion_beam.png");
    private static final Identifier BEACON_BEAM_LOCATION = VIdentifier.mc("textures/entity/beacon_beam.png");

    private static final int BEAM_SIDES = 8;
    private static final float BEAM_NEAR_RADIUS = 0.2F;

    private final BloodSphereModel sphereModel;

    public AltarInfusionRenderer(BlockEntityRendererProvider.Context context) {
        this.sphereModel = new BloodSphereModel(context.bakeLayer(ModEntitiesRender.BLOOD_SPHERE));
    }

    @Override
    public AltarInfusionRenderState createRenderState() {
        return new AltarInfusionRenderState();
    }

    @Override
    public void extractRenderState(AltarInfusionBlockEntity blockEntity, AltarInfusionRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.phase = blockEntity.getCurrentPhase();
        renderState.tips = blockEntity.getTips();
        renderState.runningTicks = blockEntity.getRunTime() + partialTick;
        renderState.player = blockEntity.getPlayer().orElse(null);

        renderState.verticalOffset = blockEntity.verticalOffset;
        renderState.rotation = blockEntity.rotation;
        renderState.prevRotation = blockEntity.prevRotation;
        renderState.partialTick = partialTick;
    }

    @Override
    public void submit(AltarInfusionRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        submitSphere(renderState, poseStack, nodeCollector);
        submitBeam(renderState, poseStack, nodeCollector);
    }

    private void submitSphere(AltarInfusionRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector) {
        poseStack.pushPose();

        float bobbing = renderState.verticalOffset;
        poseStack.translate(0.5F, 0.75F + bobbing, 0.5F);

        float rotDelta = renderState.rotation - renderState.prevRotation;
        while (rotDelta >= Math.PI) {
            rotDelta -= (float) (Math.PI * 2);
        }
        while (rotDelta < -Math.PI) {
            rotDelta += (float) (Math.PI * 2);
        }

        float interpolatedRot = renderState.prevRotation + rotDelta * renderState.partialTick;

        poseStack.mulPose(Axis.YP.rotation(-interpolatedRot));

        BloodSphereModel.BloodSphereRenderState sphereState = new BloodSphereModel.BloodSphereRenderState();
        nodeCollector.submitModel(this.sphereModel, sphereState, poseStack, RenderTypes.entitySolid(SPHERE_TEXTURE), renderState.lightCoords, OverlayTexture.NO_OVERLAY, -1, null,0, renderState.breakProgress);
        nodeCollector.submitModel(this.sphereModel, sphereState, poseStack, RenderTypes.entityTranslucentEmissive(SPHERE_TEXTURE), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1, null,0, renderState.breakProgress);

        poseStack.popPose();
    }

    private static void submitBeam(AltarInfusionRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector) {
        var phase = renderState.phase;
        if (phase != AltarInfusionBlockEntity.Phase.BEAM_CONNECT && phase != AltarInfusionBlockEntity.Phase.BEAM_PLAYER) return;

        float centerX = renderState.blockPos.getX() + 0.5f;
        float centerY = renderState.blockPos.getY() + 3f;
        float centerZ = renderState.blockPos.getZ() + 0.5f;
        float textureOffset = -(renderState.runningTicks + 1);

        poseStack.pushPose();
        poseStack.translate(0.5, 3, 0.5);

        List<BlockPos> tips = renderState.tips;
        if (tips != null) {
            for (BlockPos tip : tips) {
                float dx = tip.getX() + 0.5f - centerX;
                float dy = tip.getY() + 0.5f - centerY;
                float dz = tip.getZ() + 0.5f - centerZ;
                renderBeam(poseStack, nodeCollector, textureOffset, dx, dy, dz, 0xF000F0, true);
            }
        }

        if (phase == AltarInfusionBlockEntity.Phase.BEAM_PLAYER && renderState.player != null) {
            float dx = (float) renderState.player.getX() - centerX;
            float dy = (float) renderState.player.getY() + 1.2f - centerY;
            float dz = (float) renderState.player.getZ() - centerZ;
            renderBeam(poseStack, nodeCollector, textureOffset, dx, dy, dz, 0xF000F0, false);
        }

        poseStack.popPose();
    }

    private static void renderBeam(PoseStack poseStack, SubmitNodeCollector nodeCollector, float textureOffset, float dx, float dy, float dz, int packedLight, boolean beacon) {
        float distFlat = Mth.sqrt(dx * dx + dz * dz);
        float dist = Mth.sqrt(dx * dx + dy * dy + dz * dz);

        Identifier texture = beacon ? BEACON_BEAM_LOCATION : INFUSION_BEAM_LOCATION;
        float uvScrollV = textureOffset * 0.05f;
        float uvScrollVFar = dist / 32.0F + textureOffset * 0.05f;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotation((float) -Math.atan2(dz, dx) - (float) (Math.PI / 2)));
        poseStack.mulPose(Axis.XP.rotation((float) -Math.atan2(distFlat, dy) - (float) (Math.PI / 2)));

        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.entitySmoothCutout(texture), (pose, vertexBuilder) -> {
            Matrix4f matrix = pose.pose();

            float prevSin = 0.0F;
            float prevCos = 0.2F;
            float prevU = 0.0F;

            for (int side = 1; side <= BEAM_SIDES; side++) {
                float angle = side * ((float) Math.PI * 2F) / BEAM_SIDES;
                float sin = Mth.sin(angle) * BEAM_NEAR_RADIUS;
                float cos = Mth.cos(angle) * BEAM_NEAR_RADIUS;
                float u = (float) side / BEAM_SIDES;

                vertexBuilder.addVertex(matrix, prevSin, prevCos, 0.0F).setColor( 75, 0, 0, 255).setUv(prevU, uvScrollV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0, -1, 0);
                vertexBuilder.addVertex(matrix, prevSin * 0.5f, prevCos * 0.5f, dist).setColor(255, 0, 0, 255).setUv(prevU, uvScrollVFar).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0, -1, 0);
                vertexBuilder.addVertex(matrix, sin * 0.5f, cos * 0.5f, dist).setColor(255, 0, 0, 255).setUv(u, uvScrollVFar).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0, -1, 0);
                vertexBuilder.addVertex(matrix, sin, cos, 0.0F).setColor( 75, 0, 0, 255).setUv(u, uvScrollV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0, -1, 0);

                prevSin = sin;
                prevCos = cos;
                prevU = u;
            }
        });

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(AltarInfusionBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    public static class AltarInfusionRenderState extends BlockEntityRenderState {
        public AltarInfusionBlockEntity.Phase phase;
        @Nullable
        public List<BlockPos> tips;
        public float runningTicks;
        @Nullable
        public Player player;

        public float verticalOffset;
        public float rotation;
        public float prevRotation;
        public float partialTick;
    }
}
