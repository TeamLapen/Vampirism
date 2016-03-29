package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.potion.FakeNightVisionPotion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles all events used in central parts of the mod
 */
public class ModEventHandler {
    private final static String TAG = "EventHandler";

    @SubscribeEvent
    public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.modID.equalsIgnoreCase(REFERENCE.MODID)) {
            VampirismMod.log.i(TAG, "Configuration (%s) changed", e.configID);
            Configs.onConfigurationChanged();
            Balance.onConfigurationChanged();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onInitMapGen(InitMapGenEvent event) {
        if (event.type.equals(InitMapGenEvent.EventType.VILLAGE) && Configs.village_modify) {
            if (event.newGen != event.originalGen) {
                VampirismMod.log.w("VillageGen", "The village map generator was overwritten by another mod. There might be a problem! \n The new generator class is " + event.newGen.getClass().getCanonicalName());
            }
            ModVillages.modifyVillageSize(event.newGen);
        }
    }

    @SubscribeEvent
    public void onPlayerJoinedWorld(EntityJoinWorldEvent entityJoinWorldEvent) {
        if ((entityJoinWorldEvent.entity instanceof EntityPlayer)) {
            if (VampirismMod.proxy.isClientPlayerNull() || VampirismMod.proxy.isPlayerThePlayer((EntityPlayer) entityJoinWorldEvent.entity)) {
                //Did not find a better position to place this, since onPostInit is to early
                FakeNightVisionPotion.replaceNightVision();
            }
        }
    }
}
