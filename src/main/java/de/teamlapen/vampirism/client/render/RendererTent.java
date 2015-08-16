package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.ModelTent;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by Max on 16.08.2015.
 */
public class RendererTent extends TileEntitySpecialRenderer {

    private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/tent.png");
    private ModelTent model;

    public RendererTent() {
        model = new ModelTent();
    }

    private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
        int meta = 2;
        if (world != null)
            meta = world.getBlockMetadata(x, y, z);
        GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float p_147500_8_) {
        bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glPushMatrix();
        adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        Tessellator.instance.setBrightness(5);
        Tessellator.instance.setColorOpaque(0, 0, 0);
        model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
}
