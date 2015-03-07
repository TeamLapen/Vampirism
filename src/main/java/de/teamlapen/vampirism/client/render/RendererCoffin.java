package de.teamlapen.vampirism.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RendererCoffin extends TileEntitySpecialRenderer 
{
	//private ModelCoffin model;
	//private ResourceLocation texture;
	
	public RendererCoffin() {
		//model = new ModelCoffin;
		//texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/coffin.png");
	}
	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
	}
	@Override
	public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_,
			double p_147500_4_, double p_147500_6_, float p_147500_8_) {
		// TODO Rendering
		
	}
}
