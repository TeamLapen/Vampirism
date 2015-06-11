package de.teamlapen.vampirism.client.render;

import org.lwjgl.opengl.GL11;

import de.teamlapen.vampirism.client.model.ModelCoffin;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RendererCoffin extends TileEntitySpecialRenderer {
	private ModelCoffin model;
	private ResourceLocation[] textures = new ResourceLocation[16];
	
	 public static final String[] colors = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
	
	private final int maxLidPos = 61;

	public RendererCoffin() {
		model = new ModelCoffin();
		for(int i = 0; i < colors.length; i++) {
			Logger.i("RendererCoffin", String.format("Adding coffin texture %s to textures[%s]", colors[i], i));
			textures[i] = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/coffin/coffin_" + colors[i] + ".png");
		}
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		GL11.glRotatef(meta * (-90), 0.0F, 1.0F, 0.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z,
			float scale) {
		TileEntityCoffin tile = (TileEntityCoffin) te;
		if (te instanceof TileEntityCoffin)
			if ((te.getBlockMetadata() & (-8)) == 0) {
				// Logger.i("RendererCoffin",
				// String.format("Not rendering coffin at x=%d, y=%d, z=%d",
				// te.xCoord, te.yCoord, te.zCoord));
				return;
			}
		//Calculate lid position
		boolean occupied = (te.getBlockMetadata() & 4) != 0;
		
		if(!occupied && tile.lidPos > 0)
			tile.lidPos--;
		else if(occupied && tile.lidPos < maxLidPos)
			tile.lidPos++;
//		Logger.i("RendererCoffin", String.format("Rendering at x=%s, y=%s, z=%s, occupied=%s, lidpos=%s", te.xCoord, te.yCoord, te.zCoord, occupied, tile.lidPos));
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(textures[((TileEntityCoffin) te).color]);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.rotateLid(calcLidAngle(tile.lidPos));
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private float calcLidAngle(int pos) {
		if(pos==maxLidPos)
			return 0.0F;
		else if(pos==0)
			return (float) (0.75F * Math.PI);
		return (float) (-Math.pow(1.02, pos) + 1 + 0.75*Math.PI);
	}
}
