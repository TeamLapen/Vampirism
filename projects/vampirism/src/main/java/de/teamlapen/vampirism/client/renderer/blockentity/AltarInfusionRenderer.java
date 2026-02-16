package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.blocks.BloodSphereModel;
import de.teamlapen.vampirism.common.world.blockentity.AltarInfusionBlockEntity;
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
        nodeCollector.submitModel(this.sphereModel, sphereState, poseStack, RenderTypes.entitySolid(SPHERE_TEXTURE), renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0xF000F0,null,0, null);

        poseStack.popPose();
    }

    private void submitBeam(AltarInfusionRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector) {
        AltarInfusionBlockEntity.Phase phase = renderState.phase;
        if (phase != AltarInfusionBlockEntity.Phase.BEAM_CONNECT && phase != AltarInfusionBlockEntity.Phase.BEAM_PLAYER) {
            return; // Render the beam only when the ritual is running
        }

        BlockPos blockPos = renderState.blockPos;
        float centerX = blockPos.getX() + 0.5f;
        float centerY = blockPos.getY() + 3.0f;
        float centerZ = blockPos.getZ() + 0.5f;

        poseStack.pushPose();
        poseStack.translate(0.5, 3.0, 0.5);

        float animationOffset = -(renderState.runningTicks + renderState.partialTick);

        List<BlockPos> tips = renderState.tips;
        if (tips != null && !tips.isEmpty()) {
            for (BlockPos tip : tips) {
                float dx = tip.getX() + 0.5f - centerX;
                float dy = tip.getY() + 0.5f - centerY;
                float dz = tip.getZ() + 0.5f - centerZ;
                renderBeam(poseStack, nodeCollector, animationOffset, dx, dy, dz, renderState.lightCoords, true);
            }
        }

        if (phase == AltarInfusionBlockEntity.Phase.BEAM_PLAYER) {
            Player player = renderState.player;
            if (player != null) {
                float dx = (float) player.getX() - centerX;
                float dy = (float) player.getY() + 1.2f - centerY;
                float dz = (float) player.getZ() - centerZ;
                renderBeam(poseStack, nodeCollector, animationOffset, dx, dy, dz, renderState.lightCoords, false);
            }
        }

        poseStack.popPose();
    }

    private void renderBeam(PoseStack poseStack, SubmitNodeCollector nodeCollector, float tickOffset, float dx, float dy, float dz, int packedLight, boolean beacon) {
        float distFlat = Mth.sqrt(dx * dx + dz * dz);
        float dist = Mth.sqrt(dx * dx + dy * dy + dz * dz);

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotation((float) (-Math.atan2(dz, dx)) - (float) Math.PI / 2F));
        poseStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(distFlat, dy)) - (float) Math.PI / 2F));

        float texStart = tickOffset * 0.05f;
        float texEnd = dist / 32.0F + texStart;

        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.entitySmoothCutout(beacon ? BEACON_BEAM_LOCATION : INFUSION_BEAM_LOCATION), (pose, vertexBuilder) -> {
            Matrix4f matrix = pose.pose();

            float prevX = 0.0F;
            float prevY = 0.2F;
            float prevU = 0.0F;

            for (int i = 1; i <= 8; i++) {
                float angle = (float) (i * Math.PI * 2F / 8.0F);
                float x = Mth.sin(angle) * 0.2F;
                float y = Mth.cos(angle) * 0.2F;
                float u = i / 8.0F;

                vertexBuilder.addVertex(matrix, prevX, prevY, 0.0F)
                        .setColor(75, 0, 0, 255)
                        .setUv(prevU, texStart)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, 0.0F, -1.0F, 0.0F);

                vertexBuilder.addVertex(matrix, prevX * 0.5f, prevY * 0.5f, dist)
                        .setColor(255, 0, 0, 255)
                        .setUv(prevU, texEnd)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, 0.0F, -1.0F, 0.0F);

                vertexBuilder.addVertex(matrix, x * 0.5f, y * 0.5f, dist)
                        .setColor(255, 0, 0, 255)
                        .setUv(u, texEnd)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, 0.0F, -1.0F, 0.0F);

                vertexBuilder.addVertex(matrix, x, y, 0.0F)
                        .setColor(75, 0, 0, 255)
                        .setUv(u, texStart)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, 0.0F, -1.0F, 0.0F);

                prevX = x;
                prevY = y;
                prevU = u;
            }

            poseStack.popPose();
        });
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
