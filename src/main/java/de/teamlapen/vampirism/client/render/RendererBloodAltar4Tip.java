package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.ModelBloodAltar4Tip;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Temp placeholder renderer, will be replaced later
 * 
 * @author Max
 *
 */
@SideOnly(Side.CLIENT)
public class RendererBloodAltar4Tip extends TileEntitySpecialRenderer {

	public static final String textureLoc = REFERENCE.MODID + ":textures/blocks/bloodAltar4Tip.png";
	private final ModelBloodAltar4Tip model;
	private final ResourceLocation texture;

	public RendererBloodAltar4Tip() {
		model = new ModelBloodAltar4Tip();
		texture = new ResourceLocation(textureLoc);
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		if (world != null) {
			int meta = world.getBlockMetadata(x, y, z);
			GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
		}

	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float par5) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(texture);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

}
