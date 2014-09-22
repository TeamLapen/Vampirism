package de.teamlapen.vampirism.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import de.teamlapen.vampirism.client.model.ModelBloodAltarEmpty;
import de.teamlapen.vampirism.client.model.ModelBloodAltarFull;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class RendererBloodAltar extends TileEntitySpecialRenderer {

	// The model of your block
	private final ModelBloodAltarEmpty modelEmpty;
	private final ModelBloodAltarFull modelFull;
	private final ResourceLocation textureEmpty, textureFull;

	public RendererBloodAltar() {
		modelEmpty = new ModelBloodAltarEmpty();
		textureEmpty = new ResourceLocation(REFERENCE.MODID
				+ ":textures/blocks/bloodAltarEmpty.png");
		
		modelFull = new ModelBloodAltarFull();
		textureFull = new ResourceLocation(REFERENCE.MODID
				+ ":textures/blocks/bloodAltarEmpty.png");
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z,
			float scale) {
		boolean hasSword = ((TileEntityBloodAltar) te).isOccupied();
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		if(hasSword)
			bindTexture(textureFull);
		else
			bindTexture(textureEmpty);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord,
				te.zCoord);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		if(hasSword)
			modelFull.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		else
			modelEmpty.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
