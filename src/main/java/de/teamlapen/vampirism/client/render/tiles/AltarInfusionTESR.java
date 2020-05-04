package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.tileentity.AltarInfusionTileEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders the beams for the altar of infusion
 */
@OnlyIn(Dist.CLIENT)
public class AltarInfusionTESR extends VampirismTESR<AltarInfusionTileEntity> {


    private final ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation(REFERENCE.MODID, "textures/entity/infusion_beam.png");
    private final ResourceLocation beaconBeamTexture = new ResourceLocation("textures/entity/beacon_beam.png");

    public AltarInfusionTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(AltarInfusionTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLight, int combinedOverlay) {
        // Render the beams if the ritual is running
        AltarInfusionTileEntity.PHASE phase = te.getCurrentPhase();
        if (phase == AltarInfusionTileEntity.PHASE.BEAM1 || phase == AltarInfusionTileEntity.PHASE.BEAM2) {
            // Calculate center coordinates
            float cX = te.getPos().getX() + 0.5f;
            float cY = te.getPos().getY() + 3f;
            float cZ = te.getPos().getZ() + 0.5f;
            matrixStack.push();
            matrixStack.translate(0.5, 3, 0.5);
            BlockPos[] tips = te.getTips();
            for (BlockPos tip : tips) {
                this.renderBeam(matrixStack, iRenderTypeBuffer, -(te.getRunningTick() + partialTicks), tip.getX() + 0.5f - cX, tip.getY() + 0.5f - cY, tip.getZ() + 0.5f - cZ, combinedLight, true);
            }

            if (phase == AltarInfusionTileEntity.PHASE.BEAM2) {
                PlayerEntity p = te.getPlayer();
                if (p != null) {
                    this.renderBeam(matrixStack, iRenderTypeBuffer, -(te.getRunningTick() + partialTicks), (float) p.getPosX() - cX, (float) p.getPosY() + 1.2f - cY, (float) p.getPosZ() - cZ, combinedLight, false);
                }
            }
            matrixStack.pop();


        }
    }

    /**
     * Renders a beam in the world, similar to the dragon healing beam
     */
    private void renderBeam(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, float dx, float dy, float dz, int packedLight, boolean beacon) {

        float distFlat = MathHelper.sqrt(dx * dx + dz * dz);
        float dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        matrixStack.push();
        matrixStack.rotate(Vector3f.YP.rotation((float) (-Math.atan2(dz, dx)) - ((float) Math.PI / 2F)));
        matrixStack.rotate(Vector3f.XP.rotation((float) (-Math.atan2(distFlat, dy)) - ((float) Math.PI / 2F)));
        IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.entitySmoothCutout(beacon ? beaconBeamTexture : enderDragonCrystalBeamTextures));
        float f2 = partialTicks * 0.05f;
        float f3 = dist / 32.0F + partialTicks * 0.05f;
        float f4 = 0.0F;
        float f5 = 0.2F;
        float f6 = 0.0F;
        MatrixStack.Entry matrixstack$entry = matrixStack.getLast();
        Matrix4f matrix4f = matrixstack$entry.getPositionMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormalMatrix();

        for (int j = 1; j <= 8; ++j) {
            float f7 = MathHelper.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
            float f8 = MathHelper.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
            float f9 = (float) j / 8.0F;
            ivertexbuilder.pos(matrix4f, f4 * 1F, f5 * 1F, 0.0F).color(75, 0, 0, 255).tex(f6, f2).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.pos(matrix4f, f4 * 0.5f, f5 * 0.5f, dist).color(255, 0, 0, 255).tex(f6, f3).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.pos(matrix4f, f7 * 0.5f, f8 * 0.5f, dist).color(255, 0, 0, 255).tex(f9, f3).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.pos(matrix4f, f7 * 1F, f8 * 1F, 0.0F).color(75, 0, 0, 255).tex(f9, f2).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }

        matrixStack.pop();

    }


}
