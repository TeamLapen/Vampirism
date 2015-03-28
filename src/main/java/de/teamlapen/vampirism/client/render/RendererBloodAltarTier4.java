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
public class RendererBloodAltarTier4 extends TileEntitySpecialRenderer {

	private final ModelBloodAltarTier4 model;
	private final ResourceLocation texture;
	private static final ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
	private static final ResourceLocation beaconBeamTexture = new ResourceLocation("textures/entity/beacon_beam.png");

	public RendererBloodAltarTier4() {
		model = new ModelBloodAltarTier4();
		texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/bloodAltarTier4.png");
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
	}

	/**
	 * Renders a beam in the world, similar to the dragon healing beam
	 * @param relX startX relative to the player
	 * @param relY
	 * @param relZ
	 * @param centerX startX in world
	 * @param centerY
	 * @param centerZ
	 * @param targetX targetX in world
	 * @param targetY
	 * @param targetZ
	 * @param tickStuff used to move the beam, use the last param of {@link #renderTileEntityAt(TileEntity, double, double, double, float)} for that
	 * @param beacon whether it should be a beacon or a dragon style beam
	 */
	private void renderBeam(double relX, double relY, double relZ, double centerX, double centerY, double centerZ, double targetX, double targetY, double targetZ, float tickStuff, boolean beacon) {
		float f2 = 50000;
		float f3 = MathHelper.sin(f2 * 0.2F) / 2.0F + 0.5F;
		f3 = (f3 * f3 + f3) * 0.2F;
		float wayX = (float) (targetX - centerX);
		float wayY = (float) (targetY - centerY);
		float wayZ = (float) (targetZ - centerZ);
		float distFlat = MathHelper.sqrt_float(wayX * wayX + wayZ * wayZ);
		float dist = MathHelper.sqrt_float(wayX * wayX + wayY * wayY + wayZ * wayZ);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) relX, (float) relY, (float) relZ);
		GL11.glRotatef((float) (-Math.atan2(wayZ, wayX)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef((float) (-Math.atan2(distFlat, wayY)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
		Tessellator tessellator = Tessellator.instance;
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_CULL_FACE);
		if (beacon) {
			this.bindTexture(beaconBeamTexture);
		} else {
			this.bindTexture(enderDragonCrystalBeamTextures);
		}
		GL11.glColor3d(1.0F, 0.0F, 0.0F);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		float f9 = -(tickStuff * 0.005F);
		float f10 = MathHelper.sqrt_float(wayX * wayX + wayY * wayY + wayZ * wayZ) / 32.0F + f9;
		tessellator.startDrawing(5);
		//Add all 2*8 vertex/corners
		byte b0 = 8;
		for (int i = 0; i <= b0; ++i) {
			float f11 = 0.2F * (MathHelper.sin(i % b0 * (float) Math.PI * 2.0F / b0) * 0.75F);
			float f12 = 0.2F * (MathHelper.cos(i % b0 * (float) Math.PI * 2.0F / b0) * 0.75F);
			float f13 = i % b0 * 1.0F / b0;
			tessellator.setColorOpaque(255, 0, 0);
			tessellator.addVertexWithUV((f11), (f12), 0.0D, f13, f10);
			if (!beacon) {
				tessellator.setColorOpaque_I(16777215);
			}

			tessellator.addVertexWithUV(f11, f12, dist, f13, f9);
		}

		tessellator.draw();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glShadeModel(GL11.GL_FLAT);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float par5) {
		//Render the altar itself
		TileEntityBloodAltarTier4 te4 = (TileEntityBloodAltarTier4) te;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(texture);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		
		//Render the beams if the ritual is running
		PHASE phase = te4.getPhase();
		if (phase == PHASE.BEAM1 || phase == PHASE.BEAM2) {
			x += 0.5;
			y += 3;
			z += 0.5;
			//Calculate center coordinates
			double cX = te.xCoord + 0.5;
			double cY = te.yCoord + 3;
			double cZ = te.zCoord + 0.5;
			try {
				ChunkCoordinates[] tips = te4.getTips();
				for (int i = 0; i < tips.length; i++) {
					this.renderBeam(x, y, z, cX, cY, cZ, tips[i].posX + 0.5, tips[i].posY + 0.5, tips[i].posZ + 0.5, te4.getRunningTick() + par5, false);
				}
				if (phase == PHASE.BEAM2) {
					EntityPlayer p = te4.getPlayer();
					if (p != null) {
						this.renderBeam(0, -0.5, 0, p.posX, p.posY, p.posZ, cX, cY + 0.2, cZ, -(te4.getRunningTick() + par5), true);
					}
				}
			} catch (NullPointerException e) {
			}

		}

	}

}
