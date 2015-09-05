package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.gui.GUISleepCoffin;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.BatSkill;
import de.teamlapen.vampirism.entity.player.skills.Skills;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import java.nio.FloatBuffer;
import java.util.List;

/**
 * Rendering handler used for rendering and render transformation e.g. bat transformation
 * 
 * @author Maxanier
 *
 */
public class RenderHandler {
	private static final int ENTITY_NEAR_SQ_DISTANCE = 100;
	private EntityBat entityBat;
	private final Minecraft mc;
	private float eyeHeight;
	private float ySize;
	private boolean shiftedPosY;
	private int entityCooldownTicks;
	private int entityDisplayListId = 0;
	private int entitySphereListId = 0;
	private final int COMPILE_ENTITY_COOLDOWN;
	private final int ENTITY_RADIUS;
	private final int ENTITY_MIN_SQ_RADIUS;
	private final int BLOOD_VISION_FADE_TICKS = 80;
	private final int VAMPIRE_BIOME_FADE_TICKS = 160;
	private FloatBuffer fogColorBuffer;
	private int bloodVisionTicks = 0;
	private int vampireBiomeTicks = 0;

	public RenderHandler(Minecraft mc) {
		this.mc = mc;
		ENTITY_RADIUS = BALANCE.VAMPIRE_PLAYER_BLOOD_VISION_DISTANCE;
		ENTITY_MIN_SQ_RADIUS = BALANCE.VAMPIRE_PLAYER_BLOOD_VISION_MIN_DISTANCE * BALANCE.VAMPIRE_PLAYER_BLOOD_VISION_MIN_DISTANCE;
		COMPILE_ENTITY_COOLDOWN = Configs.blood_vision_recompile_ticks;
		entityDisplayListId = GL11.glGenLists(2);
		entitySphereListId = entityDisplayListId + 1;
		this.buildEntitySphere();
	}

	/**
	 * Builds and saves a sphere for entitys
	 */
	private void buildEntitySphere() {
		Sphere sphere = new Sphere();
		sphere.setDrawStyle(GLU.GLU_FILL);
		// GLU_SMOOTH will try to smoothly apply lighting
		sphere.setNormals(GLU.GLU_SMOOTH);
		sphere.setOrientation(GLU.GLU_OUTSIDE);

		// Create a new list to hold our sphere data.
		GL11.glNewList(entitySphereListId, GL11.GL_COMPILE);
		// binds the texture
		// ResourceLocation rL = new ResourceLocation(MagicBeans.MODID+":textures/entities/sphere.png");
		// Minecraft.getMinecraft().getTextureManager().bindTexture(rL);

		sphere.draw(0.5F, 32, 32);
		GL11.glEndList();

	}

	/**
	 * Compiles a render list of the entitys nearby
	 */
	private void compileEntitys() {
		GL11.glNewList(entityDisplayListId, GL11.GL_COMPILE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		WorldClient world = this.mc.theWorld;

		EntityClientPlayerMP player = this.mc.thePlayer;
		if ((world == null) || (player == null))
			return;

		List list = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(ENTITY_RADIUS, ENTITY_RADIUS, ENTITY_RADIUS));
		for (Object o : list) {
			if (o instanceof EntityCreature || o instanceof EntityPlayer) {
				EntityLivingBase e = (EntityLivingBase) o;
				int distance = (int) e.getDistanceSqToEntity(player);
				if (distance <= ENTITY_NEAR_SQ_DISTANCE && (distance >= ENTITY_MIN_SQ_RADIUS)) {
					// ||!player.canEntityBeSeen(e)
					renderEntity(e, (((float) distance - ENTITY_MIN_SQ_RADIUS) / (ENTITY_NEAR_SQ_DISTANCE - ENTITY_MIN_SQ_RADIUS)));
				} else if (distance > ENTITY_NEAR_SQ_DISTANCE) {
					renderEntity(e, 1F);
				}

			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEndList();
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (mc.theWorld == null)
			return;
		if (VampirePlayer.get(mc.thePlayer).getVision() != 2||VampirePlayer.get(mc.thePlayer).gettingSundamage()) {
			if (bloodVisionTicks > 0) {
				bloodVisionTicks--;
			}
			if(vampireBiomeTicks>10&&bloodVisionTicks==15){
				bloodVisionTicks=0;
			}
		} else if (!Configs.disable_blood_vision) {


			if (bloodVisionTicks < BLOOD_VISION_FADE_TICKS) {
				bloodVisionTicks++;

			}
			entityCooldownTicks--;

			if (entityCooldownTicks < 1) {
				this.compileEntitys();
				entityCooldownTicks = COMPILE_ENTITY_COOLDOWN;
			}

		}

		if(Configs.render_fog_vampire_biome&& mc.theWorld.provider.dimensionId!= VampirismMod.castleDimensionId&&Helper.isEntityInVampireBiome(mc.thePlayer)){
			if(vampireBiomeTicks< VAMPIRE_BIOME_FADE_TICKS){
				vampireBiomeTicks++;
			}
		}
		else{
			if(vampireBiomeTicks>0){
				vampireBiomeTicks--;
			}
		}
	}

	@SubscribeEvent
	public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
		if (event.entity instanceof EntityPlayer) {
			if (bloodVisionTicks>0||vampireBiomeTicks>10) {
				event.density = 1.0F;
				event.setCanceled(true);
			}
		}

	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		if (event.gui instanceof GuiSleepMP && VampirePlayer.get(mc.thePlayer).sleepingCoffin) {
			event.gui = new GUISleepCoffin();
		}
	}

	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		if (VampirePlayer.get(mc.thePlayer).isSkillActive(Skills.batMode)) {
			event.setCanceled(true);
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
			GL11.glTranslated(
					1 * (d0 - RenderManager.renderPosX),
					1
							* (d1 - RenderManager.renderPosY)
							+ (event.entityPlayer == Minecraft.getMinecraft().thePlayer
									&& !((Minecraft.getMinecraft().currentScreen instanceof GuiInventory || Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) && RenderManager.instance.playerViewY == 180.0F) ? (BatSkill.BAT_HEIGHT + 0.2 - event.entityPlayer.yOffset)
									: 0D), 1 * (d2 - RenderManager.renderPosZ));
			renderer.doRender(entityBat, 0, 0, 0, f1, event.partialRenderTick);
			GL11.glPopMatrix();
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		if (mc.theWorld == null)
			return;

		if (VampirePlayer.get(mc.thePlayer).getVision() == 2 && !VampirePlayer.get(mc.thePlayer).gettingSundamage()) {
			double doubleX = this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * event.partialTicks;

			double doubleY = this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * event.partialTicks;

			double doubleZ = this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * event.partialTicks;

			GL11.glPushMatrix();
			GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
			GL11.glCallList(entityDisplayListId);
			GL11.glPopMatrix();

		}
		if(vampireBiomeTicks>0){
			renderVampireBiomeFog(vampireBiomeTicks);
		}
		if(bloodVisionTicks>0){
			renderBloodVisionFog(bloodVisionTicks);
		}



	}

	private void renderBloodVisionFog(int ticks) {

		float f = ((float) BLOOD_VISION_FADE_TICKS) / (float) ticks;
		GL11.glPushMatrix();
		boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
		if (!fog)
			GL11.glEnable(GL11.GL_FOG);
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
		GL11.glFogf(GL11.GL_FOG_START, 4.0F * f);
		GL11.glFogf(GL11.GL_FOG_END, 5.5F * f);
		GL11.glNormal3f(0.0F, -1.0F, 0.0F);
		GL11.glColor4f(1F, 1F, 1F, 1.0F);
		GL11.glFogf(GL11.GL_FOG_DENSITY, 1.0F);
		if (!fog)
			GL11.glDisable(GL11.GL_FOG);
		GL11.glPopMatrix();
	}


	private void renderVampireBiomeFog(int ticks) {

		float f = ((float) VAMPIRE_BIOME_FADE_TICKS) / (float) ticks/1.5F;
		GL11.glPushMatrix();
		boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
		if (!fog)
			GL11.glEnable(GL11.GL_FOG);
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
		GL11.glFogf(GL11.GL_FOG_START, 15.0F * f);
		GL11.glFogf(GL11.GL_FOG_END, 50.5F * f);
		GL11.glNormal3f(0.0F, -1.0F, 0.0F);
		GL11.glColor4f(1F, 1F, 1F, 1.0F);
		GL11.glFogf(GL11.GL_FOG_DENSITY, 1.0F);
		if (!fog)
			GL11.glDisable(GL11.GL_FOG);
		GL11.glPopMatrix();
	}

	/**
	 * Renders the sphere around the give entity
	 * 
	 * @param entity
	 * @param f
	 */
	private void renderEntity(EntityLivingBase entity, float f) {
		float red = 1.0F;
		float green = 0.0F;
		float blue = 0.0F;

		if (entity instanceof EntityCreature) {
			if (VampireMob.get((EntityCreature) entity).isVampire()) {
				red = 0.23127F;
				green = 0.04313F;
				blue = 0.04313F;

			} else if (!(VampireMob.get((EntityCreature) entity).getBlood() > 0)) {
				red = 0.039215F;
				green = 0.0745F;
				blue = 0.1647F;
			}
		} else if (entity instanceof EntityPlayer) {
			if (VampirePlayer.get((EntityPlayer) entity).getLevel() > 0) {
				red = 0.039215F;
				green = 0.0745F;
				blue = 0.1647F;
			}
		}
		GL11.glPushMatrix();
		GL11.glTranslated(entity.posX, entity.posY + entity.height / 2, entity.posZ);
		GL11.glScalef(entity.width * 1.5F, entity.height * 1.5F, entity.width * 1.5F);
		GL11.glColor4f(red, green, blue, 0.5F * f);
		GL11.glCallList(entitySphereListId);
		GL11.glPopMatrix();

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

	/**
	 * Update and return fogColorBuffer with the RGBA values passed as arguments
	 */
	private FloatBuffer getFogColorBuffer() {
		if(this.fogColorBuffer==null){
			this.fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
			this.fogColorBuffer.put(1).put(1).put(1);
			this.fogColorBuffer.flip();
		}
		return this.fogColorBuffer;
	}
}
