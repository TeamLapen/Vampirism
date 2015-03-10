package de.teamlapen.vampirism.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class VampireHudOverlay extends Gui {

	private Minecraft mc;
	private final ResourceLocation icons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/icons.png");
	private static float renderRed;

	public VampireHudOverlay(Minecraft mc) {
		this.mc = mc;
	}

	@SubscribeEvent
	public void onRenderCrosshair(RenderGameOverlayEvent.Pre event) {
		if (event.type != ElementType.CROSSHAIRS) {
			return;
		}

		MovingObjectPosition p = Minecraft.getMinecraft().objectMouseOver;

		if (p != null && p.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && p.entityHit != null && p.entityHit instanceof EntityLiving) {
			VampireMob mob = VampireMob.get((EntityLiving) p.entityHit);
			if (mob == null)
				return;
			if (mob.canBeBitten() && VampirePlayer.get(Minecraft.getMinecraft().thePlayer).getLevel() > 0) {
				mc.mcProfiler.startSection("vampireFang");

				GL11.glEnable(GL11.GL_BLEND);

				this.mc.getTextureManager().bindTexture(icons);
				int left = event.resolution.getScaledWidth() / 2 - 8;
				int top = event.resolution.getScaledHeight() / 2 - 4;
				if(mob.lowEnoughHealth()){
					GL11.glColor4f(1F, 0F, 0F, 0.8F);
				}
				drawTexturedModalRect(left, top, 27, 0, 16, 16);
				GL11.glDisable(GL11.GL_BLEND);
				mc.mcProfiler.endSection();
				event.setCanceled(true);
			}
		}
	}
	
	
	/**
	 * Used to render the screen reddish
	 * @param event
	 */
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event){
		boolean lord=VampirePlayer.get(this.mc.thePlayer).isVampireLord();
		if(renderRed>0||lord){
			//Set the working matrix/layer to a layer directly on the screen/in front of the player
			ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		    int factor=scaledresolution.getScaleFactor();
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		    GL11.glMatrixMode(GL11.GL_PROJECTION);
		    GL11.glLoadIdentity();
		    GL11.glOrtho(0.0D,scaledresolution.getScaledWidth_double(),scaledresolution.getScaledHeight_double(), 0.0D, 1D, -1D);
		    GL11.glMatrixMode(GL11.GL_MODELVIEW);
		    GL11.glLoadIdentity();
		    GL11.glPushMatrix();
		    
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			
	    	int w = (scaledresolution.getScaledWidth() );
			int h = (scaledresolution.getScaledHeight() );
			
		    if(renderRed>0){
			    //Render a see through red square over the whole screen
		    	
				GL11.glColor4f(1F, 0F,0F,renderRed);
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex3f(0.0f,h, 0.0f);
			    GL11.glVertex3f(w,  h, 0.0f); 
			    GL11.glVertex3f( w,0.0f, 0.0f); 
			    GL11.glVertex3f(0.0f,0.0f, 0.0f); 
			    GL11.glEnd();
			    
			    /*
			     * Try later
		    	this.drawGradientRect(0, 0, w,Math.round(h/(2/renderRed)), 0xfff00000, 0x000000);
				this.drawGradientRect(0, h-Math.round(h/(2/renderRed)), w, h, 0x00000000, 0xfff00000);
				this.drawGradientRect2(0, 0, w/6, h, 0x000000, 0xfff00000);
				this.drawGradientRect2(w-w/6, 0, w, h, 0xfff00000, 0x000000);
				*/
		    }
		    else{
				this.drawGradientRect(0, 0, w, h/4, 0xfff00000, 0x000000);
				this.drawGradientRect(0, h-h/4, w, h, 0x00000000, 0xfff00000);
				this.drawGradientRect2(0, 0, w/8, h, 0x000000, 0xfff00000);
				this.drawGradientRect2(w-w/8, 0, w, h, 0xfff00000, 0x000000);
		    }
		    GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
			
	}
	
	/**
	 * Draws a rectangle with a horizontal gradient between the specified colors.
	 * Just changed the coloring from the {@link #drawGradientRect(int, int, int, int, int, int)}
	 */
	protected void drawGradientRect2(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_)
    {
        float f = (float)(p_73733_5_ >> 24 & 255) / 255.0F;
        float f1 = (float)(p_73733_5_ >> 16 & 255) / 255.0F;
        float f2 = (float)(p_73733_5_ >> 8 & 255) / 255.0F;
        float f3 = (float)(p_73733_5_ & 255) / 255.0F;
        float f4 = (float)(p_73733_6_ >> 24 & 255) / 255.0F;
        float f5 = (float)(p_73733_6_ >> 16 & 255) / 255.0F;
        float f6 = (float)(p_73733_6_ >> 8 & 255) / 255.0F;
        float f7 = (float)(p_73733_6_ & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double)p_73733_3_, (double)p_73733_2_, (double)this.zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double)p_73733_1_, (double)p_73733_2_, (double)this.zLevel);
        tessellator.addVertex((double)p_73733_1_, (double)p_73733_4_, (double)this.zLevel);
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double)p_73733_3_, (double)p_73733_4_, (double)this.zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
	
	/* Color fog, not used
	@SubscribeEvent
	public void onFogColor(EntityViewRenderEvent.FogColors event){
		event.red=1F;
		event.blue=0;
		event.green=0;
	}
	
	@SubscribeEvent
	public void onFogDensity(EntityViewRenderEvent.FogDensity event){
		event.density=0.1F;
		event.setCanceled(true);
	}
	*/
	@SubscribeEvent
	public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.EXPERIENCE) {
			return;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);

		int level = VampirePlayer.get(mc.thePlayer).getLevel();
		if (mc.playerController.gameIsSurvivalOrAdventure() && level > 0) {
			mc.mcProfiler.startSection("vampireLevel");
			// boolean flag1 = false;
			int color = Color.MAGENTA.getRGB();
			String text = "" + level;
			int x = (event.resolution.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2;
			int y = event.resolution.getScaledHeight() - 31 - 4 - 12;
			mc.fontRenderer.drawString(text, x + 1, y, 0);
			mc.fontRenderer.drawString(text, x - 1, y, 0);
			mc.fontRenderer.drawString(text, x, y + 1, 0);
			mc.fontRenderer.drawString(text, x, y - 1, 0);
			mc.fontRenderer.drawString(text, x, y, color);
			mc.mcProfiler.endSection();
		}
	}

	@SubscribeEvent
	public void onRenderFoodBar(RenderGameOverlayEvent.Pre event) {
		if (event.type != ElementType.FOOD) {
			return;
		}

		VampirePlayer p = VampirePlayer.get(mc.thePlayer);
		if (p.getLevel() > 0) {
			event.setCanceled(true);

			if (mc.playerController.gameIsSurvivalOrAdventure()) {
				VampirePlayer.BloodStats stats = p.getBloodStats();
				mc.mcProfiler.startSection("vampireBlood");

				GL11.glEnable(GL11.GL_BLEND);

				this.mc.getTextureManager().bindTexture(icons);
				int left = event.resolution.getScaledWidth() / 2 + 91;
				int top = event.resolution.getScaledHeight() - GuiIngameForge.right_height;

				for (int i = 0; i < 10; ++i) {
					int idx = i * 2 + 1;
					int x = left - i * 8 - 9;
					int y = top;

					// Draw Background
					drawTexturedModalRect(x, y, 0, 0, 9, 9);

					if (idx < stats.getBloodLevel()) {
						drawTexturedModalRect(x, y, 9, 0, 9, 9);
					} else if (idx == stats.getBloodLevel()) {
						drawTexturedModalRect(x, y, 18, 0, 9, 9);
					}
				}
				GL11.glDisable(GL11.GL_BLEND);
				mc.mcProfiler.endSection();
			}
		}
	}
	
	public static void setRenderRed(float value){
		if(value<0||value>1){
			Logger.i("Overlay", "Can't render screen red with value: "+value);
			return;
		}
		renderRed=value;
	}
}
