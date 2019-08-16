package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.special.DraculaHalloweenEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Quick and dirty
 * Only just at halloween
 * TODO 1.14 test
 */
public class HalloweenSpecial {

    private final static Logger LOGGER = LogManager.getLogger();
    private static boolean enabled = false;
    private static int render_overlay;

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean shouldEnable() {
        if (VampirismConfig.SERVER.disableHalloweenSpecial.get()) {
            return false;
        }
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        return false; //day == 31 && month == 9; TODO
    }

    public static void checkEnable() {
        enabled = shouldEnable();
        if (enabled) LOGGER.info("It's Halloween");
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
    public static void triggerOverlay(PlayerEntity target) {

        render_overlay = 150;
        ModParticles.spawnParticleClient(target.getEntityWorld(), ModParticles.halloween, target.posX, target.posY, target.posZ);
    }

    private List<UUID> blacklist = new ArrayList<>();
    private int tickTimer = 0;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!enabled) return;
        if (event.phase == TickEvent.Phase.END) return;
        if (render_overlay > 0) {
            render_overlay--;
        } else if (Minecraft.getInstance().world != null) {
            int time = (int) Minecraft.getInstance().world.getDayTime();
            if (time > 13000 && time < 13100) {
                triggerOverlay(Minecraft.getInstance().player);
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (!enabled) return;
        if (event.phase == TickEvent.Phase.END) return;

        if (enabled) {
            tickTimer++;
            if (tickTimer % 200 == 99) {
                for (ServerPlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    UUID u = p.getUniqueID();
                    if (!blacklist.contains(u)) {
                        DraculaHalloweenEntity draculaHalloween = ModEntities.special_dracula_halloween.create(p.getEntityWorld());
                        draculaHalloween.setOwnerId(u);
                        draculaHalloween.makeHide(200 + p.getRNG().nextInt(1000));
                        p.getEntityWorld().addEntity(draculaHalloween);
                        blacklist.add(u);
                    }
                }
                tickTimer = 0;
            }


        }

    }

    @SubscribeEvent
    public void onSleepInBed(PlayerSleepInBedEvent event) {
        if (enabled) {
            event.setResult(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
            event.getEntityPlayer().sendStatusMessage(new StringTextComponent("You cannot sleep on Halloween!"), false);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        blacklist.clear();
    }
}
