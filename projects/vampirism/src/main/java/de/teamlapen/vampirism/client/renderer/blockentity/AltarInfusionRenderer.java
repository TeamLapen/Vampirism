package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VIdentifier;
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

/**
 * Renders the beams for the altar of infusion
 */
public class AltarInfusionRenderer implements BlockEntityRenderer<AltarInfusionBlockEntity, AltarInfusionRenderer.AltarInfusionRenderState> {

    private static final Identifier INFUSION_BEAM_LOCATION = VIdentifier.mod("textures/entity/infusion_beam.png");
    private static final Identifier BEACON_BEAM_LOCATION = VIdentifier.mc("textures/entity/beacon_beam.png");
    
    public AltarInfusionRenderer(BlockEntityRendererProvider.Context context) {
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
        renderState.runningTicks = blockEntity.getRunningTick();
        renderState.player = blockEntity.getPlayer();
    }

    @Override
    public void submit(AltarInfusionRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        submitBeam(renderState, poseStack, nodeCollector);
    }

    private static void submitBeam(AltarInfusionRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector) {
        var phase = renderState.phase;
        if (phase == AltarInfusionBlockEntity.PHASE.BEAM1 || phase == AltarInfusionBlockEntity.PHASE.BEAM2) {
            // Calculate center coordinates
            float cX = renderState.blockPos.getX() + 0.5f;
            float cY = renderState.blockPos.getY() + 3f;
            float cZ = renderState.blockPos.getZ() + 0.5f;
            poseStack.pushPose();
            poseStack.translate(0.5, 3, 0.5);
            BlockPos[] tips = renderState.tips;
            if (tips != null) {
                for (BlockPos tip : tips) {
                    renderBeam(poseStack, nodeCollector, -(renderState.runningTicks + 1), tip.getX() + 0.5f - cX, tip.getY() + 0.5f - cY, tip.getZ() + 0.5f - cZ, 15728880, true);
                }
            }

            if (phase == AltarInfusionBlockEntity.PHASE.BEAM2) {
                Player p = renderState.player;
                if (p != null) {
                    renderBeam(poseStack, nodeCollector, -(renderState.runningTicks + 1), (float) p.getX() - cX, (float) p.getY() + 1.2f - cY, (float) p.getZ() - cZ, 15728880, false);
                }
            }

            poseStack.popPose();
        }
    }

    /**
     * Renders a beam in the world, similar to the dragon healing beam
     */
    private static void renderBeam(PoseStack poseStack, SubmitNodeCollector nodeCollector, float partialTick, float dx, float dy, float dz, int packedLight, boolean beacon) {
        float distFlat = Mth.sqrt(dx * dx + dz * dz);
        float dist = Mth.sqrt(dx * dx + dy * dy + dz * dz);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotation(((float) (-Math.atan2(dz, dx)) - ((float) Math.PI / 2F))));
        poseStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(distFlat, dy)) - ((float) Math.PI / 2F)));
        float f2 = partialTick * 0.05f;
        float f3 = dist / 32.0F + partialTick * 0.05f;

        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.entitySmoothCutout(beacon ? BEACON_BEAM_LOCATION : INFUSION_BEAM_LOCATION), (pose, vertexBuilder) -> {
            float f4 = 0.0F;
            float f5 = 0.2F;
            float f6 = 0.0F;
            for (int j = 1; j <= 8; ++j) {
                float f7 = Mth.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
                float f8 = Mth.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
                float f9 = (float) j / 8.0F;
                Matrix4f pose1 = pose.pose();
                vertexBuilder.addVertex(pose1, f4, f5, 0.0F).setColor(75, 0, 0, 255).setUv(f6, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
                vertexBuilder.addVertex(pose1, f4 * 0.5f, f5 * 0.5f, dist).setColor(255, 0, 0, 255).setUv(f6, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
                vertexBuilder.addVertex(pose1, f7 * 0.5f, f8 * 0.5f, dist).setColor(255, 0, 0, 255).setUv(f9, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
                vertexBuilder.addVertex(pose1, f7, f8, 0.0F).setColor(75, 0, 0, 255).setUv(f9, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
                f4 = f7;
                f5 = f8;
                f6 = f9;
            }
        });

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(AltarInfusionBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    public static class AltarInfusionRenderState extends BlockEntityRenderState {
        public AltarInfusionBlockEntity.PHASE phase;
        @Nullable
        public BlockPos[] tips;
        public float runningTicks;
        @Nullable
        public Player player;
    }
}
