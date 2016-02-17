package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.entity.player.vampire.SkillHandler;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.skills.BatSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handle most general rendering related stuff
 */
@SideOnly(Side.CLIENT)
public class RenderHandler {
    private final Minecraft mc;
    private EntityBat entityBat;
    private boolean batTransform_shiftedPosY = false;
    private float batTransform_ySize = 0F;
    private float batTransform_eyeHeight;

    public RenderHandler(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (VampirePlayer.get(mc.thePlayer).getSkillHandler().isSkillActive(SkillHandler.batSkill)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (VampirePlayer.get(event.entityPlayer).getSkillHandler().isSkillActive(SkillHandler.batSkill)) {
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
    public void renderTick(TickEvent.RenderTickEvent event) {
        /**
         * Render the player a little bit lower in first person and in bad mode.
         * Not sure how everything exactly works anymore, a lot of trial and error was required
         */
        if (mc.theWorld != null) {
            if (event.phase == TickEvent.Phase.START) {
                if (VampirePlayer.get(mc.thePlayer).getSkillHandler().isSkillActive(SkillHandler.batSkill) && mc.gameSettings.thirdPersonView == 0) {

                    //batTransform_ySize = -(float)mc.thePlayer.getYOffset() + BatSkill.BAT_EYE_HEIGHT ;
                    batTransform_eyeHeight = mc.thePlayer.eyeHeight;
//                        mc.thePlayer.lastTickPosY -= batTransform_ySize;
//                        mc.thePlayer.prevPosY -= batTransform_ySize;
//                        mc.thePlayer.posY -= batTransform_ySize;
                    mc.thePlayer.eyeHeight = mc.thePlayer.getDefaultEyeHeight() - BatSkill.BAT_EYE_HEIGHT + (float) mc.thePlayer.getYOffset();

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

}
