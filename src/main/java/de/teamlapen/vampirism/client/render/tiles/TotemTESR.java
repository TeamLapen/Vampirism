package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TotemTESR extends VampirismTESR<TotemTileEntity> {


    private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation(REFERENCE.MODID, "textures/entity/totem_beam.png");
    private final static int HEIGHT = 100;

    public TotemTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(TotemTileEntity te) {
        return true;
    }

    @Override
    public void render(TotemTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        RenderSystem.alphaFunc(516, 0.1f);
        float textureScale = te.shouldRenderBeam();
        if (textureScale > 0.0f) {
            long totalWorldTime = te.getWorld().getGameTime();
            int captureProgress = te.getCaptureProgress();
            float[] baseColors = te.getBaseColors();
            int offset = 0;
            if (captureProgress > 0) {
                float[] overtakeColors = te.getCapturingColors();
                offset = (captureProgress * HEIGHT) / 100;
                BeaconTileEntityRenderer.renderBeamSegment(matrixStack, iRenderTypeBuffer, TEXTURE_BEACON_BEAM, partialTicks, textureScale, totalWorldTime, 0, offset, overtakeColors, 0.2f, 0.25f);
            }
            BeaconTileEntityRenderer.renderBeamSegment(matrixStack, iRenderTypeBuffer, TEXTURE_BEACON_BEAM, partialTicks, textureScale, totalWorldTime, offset, HEIGHT - offset, baseColors, 0.2f, 0.25f);
        }
    }


}
