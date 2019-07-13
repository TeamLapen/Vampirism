package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.tileentity.TotemTile;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TotemTESR extends VampirismTESR<TotemTile> {

    private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation(REFERENCE.MODID, "textures/entity/totem_beam.png");
    private final static int HEIGHT = 100;

    @Override
    public boolean isGlobalRenderer(TotemTile te) {
        return true;
    }

    @Override
    public void render(TotemTile te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(te, x, y, z, partialTicks, destroyStage);
        GlStateManager.alphaFunc(516, 0.1F);
        this.bindTexture(TEXTURE_BEACON_BEAM);

        double textureScale = te.shouldBeamRender();


        if (textureScale > 0.0D) {
            long totalWorldTime = te.getWorld().getGameTime();
            int captureProgress = te.getCaptureProgress();
            float[] baseColors = te.getBaseColors();
            GlStateManager.disableFog();
            int offset = 0;
            if (captureProgress > 0) {
                float[] overtakeColors = te.getCapturingColors();
                offset = (captureProgress * HEIGHT) / 100;
                BeaconTileEntityRenderer.renderBeamSegment(x, y, z, partialTicks, textureScale, totalWorldTime, 0, offset, overtakeColors, 0.2D, 0.25D);
            }
            BeaconTileEntityRenderer.renderBeamSegment(x, y, z, partialTicks, textureScale, totalWorldTime, offset, HEIGHT - offset, baseColors, 0.2D, 0.25D);
        }
    }

}
