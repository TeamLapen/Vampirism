package de.teamlapen.vampirism.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4.PHASE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Temp placeholder renderer, will be replaced later
 * @author Max
 *
 */
@SideOnly(Side.CLIENT)
public class RendererBloodAltarTier4 extends TileEntitySpecialRenderer{

		private final ModelBloodAltar model;
		private final ResourceLocation texture;
		private static final ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
	    private static final ResourceLocation beaconBeamTexture = new ResourceLocation("textures/entity/beacon_beam.png");
	    
		public RendererBloodAltarTier4() {
			model = new ModelBloodAltar();
			texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/bloodAltar.png");
		}

		private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
			int meta = world.getBlockMetadata(x, y, z);
			GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
		}

		@Override
		public void renderTileEntityAt(TileEntity te, double x, double y, double z, float par5) {
			TileEntityBloodAltarTier4 te4=(TileEntityBloodAltarTier4)te;
			model.setOccupied(false);
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
			bindTexture(texture);
			GL11.glPushMatrix();
			adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
			GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
			model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
			//Helper.Reflection.getPrivateFinalField(Minecraft.class, obj, timer)
			PHASE phase=te4.getPhase();
			if(phase==PHASE.BEAM1||phase==PHASE.BEAM2){
				x+=0.5;
				y+=3;
				z+=0.5;
				double cX=te.xCoord+0.5;
				double cY=te.yCoord+3;
				double cZ=te.zCoord+0.5;
				ChunkCoordinates[] tips=te4.getTips();
				for(int i=0;i<tips.length;i++){
					this.renderBeam(x, y, z,cX,cY,cZ, tips[i].posX+0.5, tips[i].posY+0.5, tips[i].posZ+0.5,te4.getRunningTick()+par5,false);
				}
				if(phase==PHASE.BEAM2){
					EntityPlayer p=te4.getPlayer();
					if(p!=null){
						this.renderBeam(0, -0.5, 0, p.posX, p.posY,p.posZ, cX, cY+0.2,cZ,-(te4.getRunningTick()+par5),true);
					}
				}
				
				
			}
			
			
		}

		private void renderBeam(double relX,double relY,double relZ,double centerX,double centerY,double centerZ,double targetX,double targetY,double targetZ,float tickStuff, boolean beacon){
	            float f2 = 50000;//(float)p_76986_1_.healingEnderCrystal.innerRotation + p_76986_9_;
	            float f3 = MathHelper.sin(f2 * 0.2F) / 2.0F + 0.5F;
	            f3 = (f3 * f3 + f3) * 0.2F;
	            float f4 = (float)(targetX- centerX);
	            float f5 = (float)(targetY- centerY);
	            float f6 = (float)(targetZ - centerZ); 
	            float f7 = MathHelper.sqrt_float(f4 * f4 + f6 * f6);
	            float f8 = MathHelper.sqrt_float(f4 * f4 + f5 * f5 + f6 * f6);
	            GL11.glPushMatrix();
	            GL11.glTranslatef((float)relX, (float)relY, (float)relZ);
	            GL11.glRotatef((float)(-Math.atan2((double)f6, (double)f4)) * 180.0F / (float)Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
	            GL11.glRotatef((float)(-Math.atan2((double)f7, (double)f5)) * 180.0F / (float)Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
	            Tessellator tessellator = Tessellator.instance;
	            RenderHelper.disableStandardItemLighting();
	            GL11.glDisable(GL11.GL_CULL_FACE);
	            if(beacon){
		            this.bindTexture(beaconBeamTexture);
	            }
	            else{
	            	this.bindTexture(enderDragonCrystalBeamTextures);
	            }
	            GL11.glColor3d(1.0F, 0.0F, 0.0F);
	            GL11.glShadeModel(GL11.GL_SMOOTH);
	            float f9 = -(tickStuff*0.005F);
	            float f10 = MathHelper.sqrt_float(f4 * f4 + f5 * f5 + f6 * f6) / 32.0F + f9;
	            tessellator.startDrawing(5);
	            byte b0 = 8;

	            for (int i = 0; i <= b0; ++i)
	            {
	                float f11 = 0.2F* (MathHelper.sin((float)(i % b0) * (float)Math.PI * 2.0F / (float)b0) * 0.75F);
	                float f12 = 0.2F*(MathHelper.cos((float)(i % b0) * (float)Math.PI * 2.0F / (float)b0) * 0.75F);
	                float f13 = (float)(i % b0) * 1.0F / (float)b0;
	                tessellator.setColorOpaque(255, 0, 0);
	                tessellator.addVertexWithUV((double)(f11 ), (double)(f12 ), 0.0D, (double)f13, (double)f10);
	                if(!beacon){
	                	tessellator.setColorOpaque_I(16777215);
	                }
	                
	                tessellator.addVertexWithUV((double)f11, (double)f12, (double)f8, (double)f13, (double)f9);
	            }

	            tessellator.draw();
	            GL11.glEnable(GL11.GL_CULL_FACE);
	            GL11.glShadeModel(GL11.GL_FLAT);
	            RenderHelper.enableStandardItemLighting();
	            GL11.glPopMatrix();
		}
		
		


}
