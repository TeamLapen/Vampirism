package de.teamlapen.vampirism.client.render.tiles;

import de.teamlapen.vampirism.tileentity.TileTotem;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TotemTESR extends VampirismTESR<TileTotem> {

    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation(REFERENCE.MODID, "textures/entity/totem_beam.png");
    private final static int HEIGHT = 100;

    @Override
    public boolean isGlobalRenderer(TileTotem te) {
        return true;
    }

    @Override
    public void render(TileTotem te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.alphaFunc(516, 0.1F);
        this.bindTexture(TEXTURE_BEACON_BEAM);

        double textureScale = 1;//te.shouldRenderBeam();


        if (textureScale > 0.0D) {
            double totalWorldTime = te.getWorld().getTotalWorldTime();
            int captureProgress = te.getCaptureProgress();
            float[] baseColors = te.getBaseColors();
            GlStateManager.disableFog();
            int offset = 0;
            if (captureProgress > 0) {
                float[] overtakeColors = te.getCapturingColors();
                offset = (captureProgress * HEIGHT) / 100;
                TileEntityBeaconRenderer.renderBeamSegment(x, y, z, partialTicks, textureScale, totalWorldTime, 0, offset, overtakeColors, 0.2D, 0.25D);
            }
            TileEntityBeaconRenderer.renderBeamSegment(x, y, z, partialTicks, textureScale, totalWorldTime, offset, HEIGHT - offset, baseColors, 0.2D, 0.25D);
        }
    }

}
