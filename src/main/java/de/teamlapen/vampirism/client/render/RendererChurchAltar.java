package de.teamlapen.vampirism.client.render;

import org.lwjgl.opengl.GL11;

import de.teamlapen.vampirism.client.model.ModelChurchAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RendererChurchAltar extends TileEntitySpecialRenderer{

	public static final String textureLoc=REFERENCE.MODID + ":textures/blocks/churchAltar.png";
	private final ResourceLocation texture=new ResourceLocation(textureLoc);
	ModelBase model;
	
	public RendererChurchAltar(){
		model=new ModelChurchAltar();
	}
	
	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		if(world!=null){
			int meta = world.getBlockMetadata(x, y, z);
			GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
		}

	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float p_147500_8_) {
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
