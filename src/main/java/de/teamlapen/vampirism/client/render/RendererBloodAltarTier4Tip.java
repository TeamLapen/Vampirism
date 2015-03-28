package de.teamlapen.vampirism.client.render;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelBloodAltar;
import de.teamlapen.vampirism.client.model.ModelBloodAltarTier4;
import de.teamlapen.vampirism.client.model.ModelBloodAltarTier4Tip;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4.PHASE;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Temp placeholder renderer, will be replaced later
 * 
 * @author Max
 *
 */
@SideOnly(Side.CLIENT)
public class RendererBloodAltarTier4Tip extends TileEntitySpecialRenderer {

	private final ModelBloodAltarTier4Tip model;
	public static final String textureLoc=REFERENCE.MODID + ":textures/blocks/bloodAltarTier4Tip.png";
	private final ResourceLocation texture;
	
	public RendererBloodAltarTier4Tip() {
		model = new ModelBloodAltarTier4Tip();
		texture=new ResourceLocation(textureLoc);
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		if(world!=null){
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
