package de.teamlapen.vampirism.client.render.tiles;

import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Renders the beams for the altar of infusion
 */
public class AltarInfusionTESR extends VampirismTESR<TileAltarInfusion> {


    private final ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
    private final ResourceLocation beaconBeamTexture = new ResourceLocation("textures/entity/beacon_beam.png");


    @Override
    public void renderTileEntityAt(TileAltarInfusion te, double x, double y, double z, float partialTicks, int destroyStage) {
        // Render the beams if the ritual is running
        TileAltarInfusion.PHASE phase = te.getCurrentPhase();
        if (phase == TileAltarInfusion.PHASE.BEAM1 || phase == TileAltarInfusion.PHASE.BEAM2) {
            x += 0.5;
            y += 3;
            z += 0.5;
            // Calculate center coordinates
            double cX = te.getPos().getX() + 0.5;
            double cY = te.getPos().getY() + 3;
            double cZ = te.getPos().getZ() + 0.5;
            try {
                BlockPos[] tips = te.getTips();
                for (int i = 0; i < tips.length; i++) {
                    this.renderBeam(x, y, z, cX, cY, cZ, tips[i].getX() + 0.5, tips[i].getY() + 0.5, tips[i].getZ() + 0.5, te.getRunningTick() + partialTicks, false);
                }
                if (phase == TileAltarInfusion.PHASE.BEAM2) {
                    EntityPlayer p = te.getPlayer();
                    if (p != null) {
                        double rX = 0, rZ = 0;
                        double rY = -0.3;
                        double playerY = p.posY;
                        /**
                         * Work around for other players seeing the ritual
                         */
                        if (!p.equals(Minecraft.getMinecraft().thePlayer)) {
                            Entity e = Minecraft.getMinecraft().thePlayer;
                            rX += p.posX - e.posX;
                            rY += p.posY - e.posY + 1.5D;
                            rZ += p.posZ - e.posZ;
                            playerY += 1.5D;
                        }
                        this.renderBeam(rX, rY, rZ, p.posX, playerY, p.posZ, cX, cY + 0.2, cZ, -(te.getRunningTick() + partialTicks), true);
                    }
                }
            } catch (NullPointerException e) {
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
        float distFlat = MathHelper.sqrt_float(wayX * wayX + wayZ * wayZ);
        float dist = MathHelper.sqrt_float(wayX * wayX + wayY * wayY + wayZ * wayZ);
        GlStateManager.pushMatrix();
        GlStateManager.translate(relX, relY, relZ);
        GlStateManager.rotate((float) (-Math.atan2(wayZ, wayX)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (-Math.atan2(distFlat, wayY)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        if (beacon) {
            this.bindTexture(beaconBeamTexture);
        } else {
            this.bindTexture(enderDragonCrystalBeamTextures);
        }
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        float f9 = -(tickStuff * 0.005F);
        float f10 = MathHelper.sqrt_float(wayX * wayX + wayY * wayY + wayZ * wayZ) / 32.0F + f9;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
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
        GL11.glPopMatrix();
    }
}
