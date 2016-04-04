package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.BatVampireAction;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Method;

/**
 * Handle most general rendering related stuff
 */
@SideOnly(Side.CLIENT)
public class RenderHandler {
    private static final ResourceLocation saturation1 = new ResourceLocation(REFERENCE.MODID + ":shaders/saturation1.json");
    private final Minecraft mc;
    private final int VAMPIRE_BIOME_FADE_TICKS = 160;
    private EntityBat entityBat;
    private boolean batTransform_shiftedPosY = false;
    private float batTransform_ySize = 0F;
    private float batTransform_eyeHeight;
    private int vampireBiomeTicks = 0;
    private boolean doShaders = true;

    public RenderHandler(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null) return;
        if (event.phase == TickEvent.Phase.END) return;
        if (Configs.renderVampireForestFog && VReference.castleDimId != mc.theWorld.provider.getDimensionId() && Helper.isEntityInVampireBiome(mc.thePlayer)) {


            if (vampireBiomeTicks < VAMPIRE_BIOME_FADE_TICKS) {
                vampireBiomeTicks++;
            }
        } else {
            if (vampireBiomeTicks > 0) {
                vampireBiomeTicks--;
            }
        }

        if (OpenGlHelper.areShadersSupported() && doShaders) {
            if (mc.thePlayer != null && mc.thePlayer.getRNG().nextInt(10) == 3) {
                PotionEffect pe = mc.thePlayer.getActivePotionEffect(ModPotions.saturation);
                boolean active = pe != null && pe.getAmplifier() >= 2;
                EntityRenderer renderer = mc.entityRenderer;
                if (active && !renderer.isShaderActive()) {
                    //Load saturation shader if not active
                    try {
                        Method method = ReflectionHelper.findMethod(EntityRenderer.class, renderer, new String[]{"loadShader", SRGNAMES.EntityRenderer_loadShader}, ResourceLocation.class);
                        method.invoke(renderer, saturation1);
                    } catch (Exception e) {
                        doShaders = false;
                        VampirismMod.log.e("RenderHandler", e, "Failed to activate saturation shader");
                    }
                } else if (!active && renderer.isShaderActive() && renderer.getShaderGroup().getShaderGroupName().equals(saturation1.toString())) {
                    renderer.stopUseShader();
                }
            }
        }
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (event.entity instanceof EntityPlayer) {
            if (vampireBiomeTicks > 10) {
                event.density = 1.0F;
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (VampirePlayer.get(mc.thePlayer).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
    }

//    @SubscribeEvent
//    public void onEntityJoinWorld(EntityJoinWorldEvent event){
//        if(event.entity instanceof EntityPlayer){
//            VampirismMod.log.t("Player"); //TODO check
//            if(event.entity.equals(mc.thePlayer)){
//                VampirismMod.log.t("this");
//                if(Helper.isEntityInVampireBiome(mc.thePlayer)){
//                    VampirismMod.log.t("that");
//                    vampireBiomeTicks=VAMPIRE_BIOME_FADE_TICKS;
//                }
//                else{
//                    VampirismMod.log.t(""+event.entity.worldObj.provider.getWorldChunkManager().getBiomeGenAt(null,event.entity.getPosition().getX(),event.entity.getPosition().getZ(),1,1,false)[0]);
//                    vampireBiomeTicks=0;
//                }
//            }
//        }
//    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (VampirePlayer.get(event.entityPlayer).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
            if (entityBat == null) {
                entityBat = new EntityBat(event.entity.worldObj);
                entityBat.setIsBatHanging(false);
            }
            EntityPlayer player = event.entityPlayer;

            float parTick = event.partialRenderTick;
            Render renderer = mc.getRenderManager().getEntityRenderObject(entityBat);

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
            Entity entity = mc.getRenderViewEntity();
            double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) parTick;
            double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) parTick;
            double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) parTick;
            mc.getRenderManager().doRenderEntity(entityBat, d0 - d3, d1 - d4, d2 - d5, f1, event.partialRenderTick, false);

        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.theWorld == null) return;
        if (vampireBiomeTicks > 0) {
            renderVampireBiomeFog(vampireBiomeTicks);
        }
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        /**
         * Render the player a little bit lower in first person and in bad mode.
         * Not sure how everything exactly works anymore, a lot of trial and error was required
         */
        if (mc.theWorld != null) {
            if (event.phase == TickEvent.Phase.START) {
                if (VampirePlayer.get(mc.thePlayer).getActionHandler().isActionActive(VampireActions.batAction) && mc.gameSettings.thirdPersonView == 0) {

                    //batTransform_ySize = -(float)mc.thePlayer.getYOffset() + BatVampireAction.BAT_EYE_HEIGHT ;
                    batTransform_eyeHeight = mc.thePlayer.eyeHeight;
//                        mc.thePlayer.lastTickPosY -= batTransform_ySize;
//                        mc.thePlayer.prevPosY -= batTransform_ySize;
//                        mc.thePlayer.posY -= batTransform_ySize;
                    mc.thePlayer.eyeHeight = mc.thePlayer.getDefaultEyeHeight() - BatVampireAction.BAT_EYE_HEIGHT + (float) mc.thePlayer.getYOffset();

                    batTransform_shiftedPosY = true;
                }
            } else {
                if (batTransform_shiftedPosY) {
//                        batTransform_shiftedPosY = false;
//                        mc.thePlayer.lastTickPosY += batTransform_ySize;
//                        mc.thePlayer.prevPosY += batTransform_ySize;
//                        mc.thePlayer.posY += batTransform_ySize;
                    mc.thePlayer.eyeHeight = batTransform_eyeHeight;
                }
            }
        }
    }

    private void renderVampireBiomeFog(int ticks) {

        float f = ((float) VAMPIRE_BIOME_FADE_TICKS) / (float) ticks / 1.5F;
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

}
