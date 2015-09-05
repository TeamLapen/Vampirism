package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.ModelBloodAltar2;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * 
 * @author Moritz
 *
 *
 */
@SideOnly(Side.CLIENT)
public class RendererBloodAltar2 extends TileEntitySpecialRenderer {

	// The model of your block
	private final ModelBloodAltar2 model;
	private final ResourceLocation texture;

	public RendererBloodAltar2() {
		model = new ModelBloodAltar2();
		texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/bloodAltar2.png");
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		int meta = 2;
		if (world != null)
			meta = world.getBlockMetadata(x, y, z);
		GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
		TileEntityBloodAltar2 te2 = (TileEntityBloodAltar2) te;
		model.setBloodLevel((int) StrictMath.ceil(((float) te2.getBloodAmount()) / te2.getMaxBlood() * 15));
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(texture);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		model.renderBase(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		model.renderBlood(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		model.renderSphere(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
