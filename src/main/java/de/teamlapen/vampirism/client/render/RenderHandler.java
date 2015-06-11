package de.teamlapen.vampirism.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import de.teamlapen.vampirism.client.gui.GUISleepCoffin;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.BatSkill;
import de.teamlapen.vampirism.entity.player.skills.Skills;
import de.teamlapen.vampirism.util.Logger;

/**
 * Rendering handler used for rendering and render transformation e.g. bat transformation
 * @author Maxanier
 *
 */
public class RenderHandler {
	private EntityBat entityBat;
	private final Minecraft mc;
	private float eyeHeight;
	private float ySize;
	private boolean shiftedPosY;

	public RenderHandler(Minecraft mc) {
		this.mc = mc;
	}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event){
		if(event.gui instanceof GuiSleepMP&&VampirePlayer.get(mc.thePlayer).sleepingCoffin){
			event.gui=new GUISleepCoffin();
		}
	}

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Pre event) {
		if (VampirePlayer.get(event.entityPlayer).isSkillActive(Skills.batMode)) {
			event.setCanceled(true);
			if (entityBat == null) {
				entityBat = new EntityBat(event.entity.worldObj);
				entityBat.setIsBatHanging(false);
			}
			EntityPlayer player = event.entityPlayer;
			
			float parTick = event.partialRenderTick;
			Render renderer = RenderManager.instance.getEntityRenderObject(entityBat);

			// Copy values
			entityBat.prevRenderYawOffset = player.prevRenderYawOffset;
			entityBat.renderYawOffset = player.renderYawOffset;
			entityBat.ticksExisted = player.ticksExisted;
			entityBat.rotationPitch = player.rotationPitch;
			entityBat.rotationYaw = player.rotationYaw;
			entityBat.rotationYawHead = player.rotationYawHead;
			entityBat.prevRotationYaw = player.prevRotationYaw;
			entityBat.prevRotationPitch = player.prevRotationPitch;
			entityBat.prevRotationYawHead = player.prevRotationYawHead;
			entityBat.setInvisible(player.isInvisible());

			// Calculate render parameter
			float f1 = entityBat.prevRotationYaw + (entityBat.rotationYaw - entityBat.prevRotationYaw) * parTick;
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * parTick;
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * parTick;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * parTick;
			// Translate and render
			GL11.glPushMatrix();
			GL11.glTranslated(1 * (d0 - RenderManager.renderPosX),1* (d1 - RenderManager.renderPosY)
							+ (event.entityPlayer == Minecraft.getMinecraft().thePlayer&& !((Minecraft.getMinecraft().currentScreen instanceof GuiInventory || Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) && RenderManager.instance.playerViewY == 180.0F) ? (BatSkill.BAT_HEIGHT+0.2 - event.entityPlayer.yOffset): 0D),
							1 * (d2 - RenderManager.renderPosZ));
			renderer.doRender(entityBat, 0, 0, 0, f1, event.partialRenderTick);
			GL11.glPopMatrix();
		}
	}
	
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event){
		if(VampirePlayer.get(mc.thePlayer).isSkillActive(Skills.batMode)){
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent event) {

		if (mc.theWorld != null) {
			if (event.phase == TickEvent.Phase.START) {
				if (VampirePlayer.get(mc.thePlayer).isSkillActive(Skills.batMode)) {
					ySize = mc.thePlayer.yOffset - BatSkill.BAT_EYE_HEIGHT + mc.thePlayer.getDefaultEyeHeight();
					eyeHeight = mc.thePlayer.eyeHeight;
					mc.thePlayer.lastTickPosY -= ySize;
					mc.thePlayer.prevPosY -= ySize;
					mc.thePlayer.posY -= ySize;
					mc.thePlayer.eyeHeight = mc.thePlayer.getDefaultEyeHeight();

					shiftedPosY = true;
				}
			} else {
				if (shiftedPosY) {
					shiftedPosY = false;
					mc.thePlayer.lastTickPosY += ySize;
					mc.thePlayer.prevPosY += ySize;
					mc.thePlayer.posY += ySize;
					mc.thePlayer.eyeHeight = eyeHeight;
				}
			}
		}
	}
}
