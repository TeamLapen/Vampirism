package de.teamlapen.vampirism.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class VampireHudOverlay extends Gui {

	private Minecraft mc;
	private final ResourceLocation icons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/icons.png");

	public VampireHudOverlay(Minecraft mc) {
		this.mc = mc;
	}

	@SubscribeEvent
	public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) {
		//
		// We draw after the ExperienceBar has drawn. The event raised by
		// GuiIngameForge.pre()
		// will return true from isCancelable. If you call
		// event.setCanceled(true) in
		// that case, the portion of rendering which this event represents will
		// be canceled.
		// We want to draw *after* the experience bar is drawn, so we make sure
		// isCancelable() returns
		// false and that the eventType represents the ExperienceBar event.
		if (event.type != ElementType.EXPERIENCE) {
			return;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);

		int level = VampirePlayer.get(mc.thePlayer).getLevel();
		if (mc.playerController.gameIsSurvivalOrAdventure() && level > 0) {
			mc.mcProfiler.startSection("vampireLevel");
//			boolean flag1 = false;
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
				VampirePlayer.BloodStats stats=p.getBloodStats();
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
}
