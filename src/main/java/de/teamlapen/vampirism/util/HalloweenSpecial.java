package de.teamlapen.vampirism.util;

import com.google.common.collect.Maps;
import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.special.EntityDraculaHalloween;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

/**
 * Quick and dirty
 * Only just at halloween
 */
public class HalloweenSpecial {

    private static boolean enabled = false;
    private static int render_overlay;
    private Map<UUID, EntityDraculaHalloween> draculas = Maps.newHashMap();
    private int tickTimer = 0;

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean shouldEnable() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        return day == 31 && month == 9;
    }

    public static void enable() {
        enabled = true;
        VampirismMod.log.i("Halloween", "It's Halloween");
    }

    /**
     * Should only be used client side
     *
     * @return
     */
    public static boolean shouldRenderOverlay() {
        return render_overlay > 0;
    }

    /**
     * Should only be used client side
     *
     * @param target
     */
    public static void triggerOverlay(EntityPlayer target) {

        render_overlay = 150;
        VampLib.proxy.getParticleHandler().spawnParticle(target.getEntityWorld(), ModParticles.HALLOWEEN, target.posX, target.posY, target.posZ);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;

        draculas.values().removeIf(entityDraculaHalloween -> entityDraculaHalloween.isDead);
        if (enabled) {
            tickTimer++;
            if (tickTimer % 200 == 99) {
                for (EntityPlayerMP p : FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayers()) {
                    UUID u = p.getUniqueID();
                    if (!draculas.containsKey(u)) {
                        EntityDraculaHalloween draculaHalloween = new EntityDraculaHalloween(p.getEntityWorld());
                        draculaHalloween.setOwnerId(u);
                        draculaHalloween.makeHide(200 + p.getRNG().nextInt(1000));
                        p.getEntityWorld().spawnEntity(draculaHalloween);
                        draculas.put(u, draculaHalloween);
                    }
                }
                tickTimer = 0;
            }


        }

    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        draculas.clear();
    }

    @SubscribeEvent
    public void onSleepInBed(PlayerSleepInBedEvent event) {
        if (enabled) {
            event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_NOW);
            event.getEntityPlayer().sendStatusMessage(new TextComponentString("You cannot sleep on Halloween!"), false);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (render_overlay > 0) {
            render_overlay--;
        } else if (Minecraft.getMinecraft().world != null) {
            int time = (int) Minecraft.getMinecraft().world.getWorldTime();
            if (time > 13000 && time < 13100) {
                triggerOverlay(Minecraft.getMinecraft().player);
            }
        }
    }
}
