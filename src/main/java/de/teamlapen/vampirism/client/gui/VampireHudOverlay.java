package de.teamlapen.vampirism.client.gui;

import org.lwjgl.opengl.GL11;


import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import de.teamlapen.vampirism.playervampire.PlayerVampire;
import de.teamlapen.vampirism.util.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class VampireHudOverlay extends Gui{
	
	private Minecraft mc;
	
	public VampireHudOverlay(Minecraft mc){
		this.mc=mc;
	}
	
	@SubscribeEvent
	public void onRenderExperienceBar(RenderGameOverlayEvent.Pre event)
	{
	    //
	    // We draw after the ExperienceBar has drawn.  The event raised by GuiIngameForge.pre()
	    // will return true from isCancelable.  If you call event.setCanceled(true) in
	    // that case, the portion of rendering which this event represents will be canceled.
	    // We want to draw *after* the experience bar is drawn, so we make sure isCancelable() returns
	    // false and that the eventType represents the ExperienceBar event.
	    if(event.type != ElementType.EXPERIENCE)
	    {      
	      return;
	    }

	    int level=PlayerVampire.getVampireLevel(mc.thePlayer);
	    
	    Logger.i("test", ""+level);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glDisable(GL11.GL_LIGHTING);
	    
	    if (mc.playerController.gameIsSurvivalOrAdventure() && level>0)
        {
            mc.mcProfiler.startSection("vampireLevel");
            boolean flag1 = false;
            int color = flag1 ? 16777215 : 8453920;
            String text = "" + level;
            int x = (event.resolution.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2;
            int y = event.resolution.getScaledHeight() - 31 - 4;
            mc.fontRenderer.drawString(text, x + 1, y, 0);
            mc.fontRenderer.drawString(text, x - 1, y, 0);
            mc.fontRenderer.drawString(text, x, y + 1, 0);
            mc.fontRenderer.drawString(text, x, y - 1, 0);
            mc.fontRenderer.drawString(text, x, y, color);
            mc.mcProfiler.endSection();
        }
	}

}
