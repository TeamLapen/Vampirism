package de.teamlapen.vampirism.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelBloodAltarTier2;
import de.teamlapen.vampirism.client.model.ModelBloodAltarTier4;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier2;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * 
 * @author Moritz
 *
 *
 */
@SideOnly(Side.CLIENT)
public class RendererBloodAltarTier4 extends TileEntitySpecialRenderer {

	// The model of your block
	private final ModelBloodAltarTier4 model;
	private final ResourceLocation texture;

	public RendererBloodAltarTier4() {
		model = new ModelBloodAltarTier4();
		texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/bloodAltarTier4.png");
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
		TileEntityBloodAltarTier4 te2 = (TileEntityBloodAltarTier4) te;
		model.setBloodLevel((int) StrictMath.ceil(((float) te2.getBloodAmount()) / te2.getMaxBlood() * 15));
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(texture);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
