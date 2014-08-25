package de.teamlapen.vampirism.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.ModelBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;

public class RendererBloodAltar extends TileEntitySpecialRenderer {

	//TODO Model and texture
	private ModelBloodAltar bloodAltarModel = new ModelBloodAltar();
	private String texture = "";

	public RendererBloodAltar() {
		//TODO Constructor
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y,
			double z, float f) {
		// TODO Rendering code
		this.bindTexture(TextureMap.locationBlocksTexture);
		Tessellator tessellator = Tessellator.instance;
	    // Using glPushMatrix before doing our rendering, and then using glPopMatrix at the end means any "transformation"
	    // that we do (glTranslated, glRotated, et c.) does not screw up rendering in an unrelated part of the game.
	    GL11.glPushMatrix();
	    GL11.glTranslated(x, y, z); // This is necessary to make our rendering happen in the right place.
	    tessellator.startDrawingQuads();
	    //area where we can use the tessellator
	    tessellator.startDrawingQuads();
	    tessellator.addVertexWithUV(0, 0, 0, 0, 0);
	    tessellator.addVertexWithUV(0, 1, 0, 0, 1);
	    tessellator.addVertexWithUV(1, 1, 0, 1, 1);
	    tessellator.addVertexWithUV(1, 0, 0, 1, 0);

	    tessellator.addVertexWithUV(0, 0, 0, 0, 0);
	    tessellator.addVertexWithUV(1, 0, 0, 1, 0);
	    tessellator.addVertexWithUV(1, 1, 0, 1, 1);
	    tessellator.addVertexWithUV(0, 1, 0, 0, 1);

	    tessellator.draw();
	    GL11.glPopMatrix();
	}

}
