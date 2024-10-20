package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blockentity.AltarInfusionBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * Renders the beams for the altar of infusion
 */
public class AltarInfusionBESR extends VampirismBESR<AltarInfusionBlockEntity> {


    private final ResourceLocation enderDragonCrystalBeamTextures = VResourceLocation.mod("textures/entity/infusion_beam.png");
    private final ResourceLocation beaconBeamTexture = VResourceLocation.mc("textures/entity/beacon_beam.png");

    public AltarInfusionBESR(BlockEntityRendererProvider.Context context) {
    }


    @Override
    public void render(@NotNull AltarInfusionBlockEntity te, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int combinedLight, int combinedOverlay) {
        // Render the beams if the ritual is running
        AltarInfusionBlockEntity.PHASE phase = te.getCurrentPhase();
        if (phase == AltarInfusionBlockEntity.PHASE.BEAM1 || phase == AltarInfusionBlockEntity.PHASE.BEAM2) {
            // Calculate center coordinates
            float cX = te.getBlockPos().getX() + 0.5f;
            float cY = te.getBlockPos().getY() + 3f;
            float cZ = te.getBlockPos().getZ() + 0.5f;
            matrixStack.pushPose();
            matrixStack.translate(0.5, 3, 0.5);
            BlockPos[] tips = te.getTips();
            for (BlockPos tip : tips) {
                this.renderBeam(matrixStack, iRenderTypeBuffer, -(te.getRunningTick() + partialTicks), tip.getX() + 0.5f - cX, tip.getY() + 0.5f - cY, tip.getZ() + 0.5f - cZ, combinedLight, true);
            }

            if (phase == AltarInfusionBlockEntity.PHASE.BEAM2) {
                Player p = te.getPlayer();
                if (p != null) {
                    this.renderBeam(matrixStack, iRenderTypeBuffer, -(te.getRunningTick() + partialTicks), (float) p.getX() - cX, (float) p.getY() + 1.2f - cY, (float) p.getZ() - cZ, combinedLight, false);
                }
            }
            matrixStack.popPose();


        }
    }

    /**
     * Renders a beam in the world, similar to the dragon healing beam
     */
    private void renderBeam(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource renderTypeBuffer, float partialTicks, float dx, float dy, float dz, int packedLight, boolean beacon) {

        float distFlat = Mth.sqrt(dx * dx + dz * dz);
        float dist = Mth.sqrt(dx * dx + dy * dy + dz * dz);
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotation(((float) (-Math.atan2(dz, dx)) - ((float) Math.PI / 2F))));
        matrixStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(distFlat, dy)) - ((float) Math.PI / 2F)));
        VertexConsumer ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.entitySmoothCutout(beacon ? beaconBeamTexture : enderDragonCrystalBeamTextures));
        float f2 = partialTicks * 0.05f;
        float f3 = dist / 32.0F + partialTicks * 0.05f;
        float f4 = 0.0F;
        float f5 = 0.2F;
        float f6 = 0.0F;
        PoseStack.Pose matrixstack$entry = matrixStack.last();
        Matrix4f matrix4f = matrixstack$entry.pose();

        for (int j = 1; j <= 8; ++j) {
            float f7 = Mth.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
            float f8 = Mth.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
            float f9 = (float) j / 8.0F;
            ivertexbuilder.addVertex(matrix4f, f4, f5, 0.0F).setColor(75, 0, 0, 255).setUv(f6, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(matrixstack$entry, 0.0F, -1.0F, 0.0F);
            ivertexbuilder.addVertex(matrix4f, f4 * 0.5f, f5 * 0.5f, dist).setColor(255, 0, 0, 255).setUv(f6, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(matrixstack$entry, 0.0F, -1.0F, 0.0F);
            ivertexbuilder.addVertex(matrix4f, f7 * 0.5f, f8 * 0.5f, dist).setColor(255, 0, 0, 255).setUv(f9, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(matrixstack$entry, 0.0F, -1.0F, 0.0F);
            ivertexbuilder.addVertex(matrix4f, f7, f8, 0.0F).setColor(75, 0, 0, 255).setUv(f9, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(matrixstack$entry, 0.0F, -1.0F, 0.0F);
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }

        matrixStack.popPose();

    }

    @Override
    public AABB getRenderBoundingBox(AltarInfusionBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
