package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.tileentity.AltarInfusionTileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Renders the beams for the altar of infusion
 */
@OnlyIn(Dist.CLIENT)
public class AltarInfusionTESR extends VampirismTESR<AltarInfusionTileEntity> {


    private final ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
    private final ResourceLocation beaconBeamTexture = new ResourceLocation("textures/entity/beacon_beam.png");


    @Override
    public void render(AltarInfusionTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        // Render the beams if the ritual is running
        AltarInfusionTileEntity.PHASE phase = te.getCurrentPhase();
        if (phase == AltarInfusionTileEntity.PHASE.BEAM1 || phase == AltarInfusionTileEntity.PHASE.BEAM2) {
            x += 0.5;
            y += 3;
            z += 0.5;
            // Calculate center coordinates
            double cX = te.getPos().getX() + 0.5;
            double cY = te.getPos().getY() + 3;
            double cZ = te.getPos().getZ() + 0.5;

            BlockPos[] tips = te.getTips();
            for (BlockPos tip : tips) {
                this.renderBeam(x, y, z, cX, cY, cZ, tip.getX() + 0.5, tip.getY() + 0.5, tip.getZ() + 0.5, te.getRunningTick() + partialTicks, false);
            }
            if (phase == AltarInfusionTileEntity.PHASE.BEAM2) {
                PlayerEntity p = te.getPlayer();
                if (p != null) {
                    this.renderBeam(x, y, z, cX, cY, cZ, p.posX, p.posY + 1.2d, p.posZ, -(te.getRunningTick() + partialTicks), true);

                }
            }


        }
    }


    /**
     * Renders a beam in the world, similar to the dragon healing beam
     *
     * @param relX      startX relative to the player
     * @param relY
     * @param relZ
     * @param centerX   startX in world
     * @param centerY
     * @param centerZ
     * @param targetX   targetX in world
     * @param targetY
     * @param targetZ
     * @param tickStuff
     * @param beacon    whether it should be a beacon or a dragon style beam
     */
    private void renderBeam(double relX, double relY, double relZ, double centerX, double centerY, double centerZ, double targetX, double targetY, double targetZ, float tickStuff, boolean beacon) {
        float f2 = 50000;
        float f3 = MathHelper.sin(f2 * 0.2F) / 2.0F + 0.5F;
        f3 = (f3 * f3 + f3) * 0.2F;
        float wayX = (float) (targetX - centerX);
        float wayY = (float) (targetY - centerY);
        float wayZ = (float) (targetZ - centerZ);
        float distFlat = MathHelper.sqrt(wayX * wayX + wayZ * wayZ);
        float dist = MathHelper.sqrt(wayX * wayX + wayY * wayY + wayZ * wayZ);
        GlStateManager.pushMatrix();
        GlStateManager.translated(relX, relY, relZ);
        GlStateManager.rotatef((float) (-Math.atan2(wayZ, wayX)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float) (-Math.atan2(distFlat, wayY)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        if (beacon) {
            this.bindTexture(beaconBeamTexture);
        } else {
            this.bindTexture(enderDragonCrystalBeamTextures);
        }
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        float f9 = -(tickStuff * 0.005F);
        float f10 = MathHelper.sqrt(wayX * wayX + wayY * wayY + wayZ * wayZ) / 32.0F + f9;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        // Add all 2*8 vertex/corners
        byte b0 = 8;
        for (int i = 0; i <= b0; ++i) {
            float f11 = 0.2F * (MathHelper.sin(i % b0 * (float) Math.PI * 2.0F / b0) * 0.75F);
            float f12 = 0.2F * (MathHelper.cos(i % b0 * (float) Math.PI * 2.0F / b0) * 0.75F);
            float f13 = i % b0 * 1.0F / b0;
            worldRenderer.pos(f11, f12, 0).tex(f13, f10).color(255, 0, 0, 255).endVertex();
            if (beacon) {
                worldRenderer.pos(f11, f12, dist).tex(f13, f9).color(255, 0, 0, 255).endVertex();
            } else {
                worldRenderer.pos(f11, f12, dist).tex(f13, f9).color(255, 255, 255, 255).endVertex();
            }
        }

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableCull();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
