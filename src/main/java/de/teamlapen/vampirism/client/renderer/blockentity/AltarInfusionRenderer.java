package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.blockentity.AltarInfusionBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/**
 * Renders the beams for the altar of infusion
 */
public class AltarInfusionRenderer implements BlockEntityRenderer<AltarInfusionBlockEntity> {

    private final ResourceLocation INFUSION_BEAM_LOCATION = VResourceLocation.mod("textures/entity/infusion_beam.png");
    private final ResourceLocation BEACON_BEAM_LOCATION = VResourceLocation.mc("textures/entity/beacon_beam.png");
    
    public AltarInfusionRenderer(BlockEntityRendererProvider.Context context) {
    }
    
    @Override
    public void render(AltarInfusionBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Render the beams if the ritual is running
        AltarInfusionBlockEntity.PHASE phase = blockEntity.getCurrentPhase();
        if (phase == AltarInfusionBlockEntity.PHASE.BEAM1 || phase == AltarInfusionBlockEntity.PHASE.BEAM2) {
            // Calculate center coordinates
            float cX = blockEntity.getBlockPos().getX() + 0.5f;
            float cY = blockEntity.getBlockPos().getY() + 3f;
            float cZ = blockEntity.getBlockPos().getZ() + 0.5f;
            poseStack.pushPose();
            poseStack.translate(0.5, 3, 0.5);
            BlockPos[] tips = blockEntity.getTips();
            if (tips != null) {
                for (BlockPos tip : tips) {
                    this.renderBeam(poseStack, bufferSource, -(blockEntity.getRunningTick() + partialTick), tip.getX() + 0.5f - cX, tip.getY() + 0.5f - cY, tip.getZ() + 0.5f - cZ, packedLight, true);
                }
            }

            if (phase == AltarInfusionBlockEntity.PHASE.BEAM2) {
                Player p = blockEntity.getPlayer();
                if (p != null) {
                    this.renderBeam(poseStack, bufferSource, -(blockEntity.getRunningTick() + partialTick), (float) p.getX() - cX, (float) p.getY() + 1.2f - cY, (float) p.getZ() - cZ, packedLight, false);
                }
            }
            
            poseStack.popPose();
        }
    }

    /**
     * Renders a beam in the world, similar to the dragon healing beam
     */
    private void renderBeam(PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, float dx, float dy, float dz, int packedLight, boolean beacon) {
        float distFlat = Mth.sqrt(dx * dx + dz * dz);
        float dist = Mth.sqrt(dx * dx + dy * dy + dz * dz);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotation(((float) (-Math.atan2(dz, dx)) - ((float) Math.PI / 2F))));
        poseStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(distFlat, dy)) - ((float) Math.PI / 2F)));
        VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.entitySmoothCutout(beacon ? BEACON_BEAM_LOCATION : INFUSION_BEAM_LOCATION));
        float f2 = partialTick * 0.05f;
        float f3 = dist / 32.0F + partialTick * 0.05f;
        float f4 = 0.0F;
        float f5 = 0.2F;
        float f6 = 0.0F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();

        for (int j = 1; j <= 8; ++j) {
            float f7 = Mth.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
            float f8 = Mth.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
            float f9 = (float) j / 8.0F;
            vertexBuilder.addVertex(matrix4f, f4, f5, 0.0F).setColor(75, 0, 0, 255).setUv(f6, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            vertexBuilder.addVertex(matrix4f, f4 * 0.5f, f5 * 0.5f, dist).setColor(255, 0, 0, 255).setUv(f6, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            vertexBuilder.addVertex(matrix4f, f7 * 0.5f, f8 * 0.5f, dist).setColor(255, 0, 0, 255).setUv(f9, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            vertexBuilder.addVertex(matrix4f, f7, f8, 0.0F).setColor(75, 0, 0, 255).setUv(f9, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(AltarInfusionBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
