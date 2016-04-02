package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.potion.FakeNightVisionPotion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

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

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        VersionChecker.VersionInfo versionInfo = VampirismMod.instance.getVersionInfo();
        if (!versionInfo.isChecked()) VampirismMod.log.w(TAG, "Version check is not finished yet");
        if (!Configs.disable_versionCheck && versionInfo.isNewVersionAvailable()) {
            if (!MinecraftServer.getServer().isDedicatedServer() || MinecraftServer.getServer().getConfigurationManager().getOppedPlayers().getEntry(event.player.getGameProfile()) != null || event.player.getRNG().nextInt(5) == 0) {
                if (event.player.getRNG().nextInt(4) == 0) {
                    VersionChecker.Version newVersion = versionInfo.getNewVersion();
                    //Inspired by @Vazikii's useful message
                    event.player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.outdated", versionInfo.getCurrentVersion().name, newVersion.name));
                    String template = StatCollector.translateToLocal("text.vampirism.update_message");
                    template = template.replaceAll("@download@", newVersion.getUrl() == null ? versionInfo.getHomePage() : newVersion.getUrl()).replaceAll("@forum@", versionInfo.getHomePage());
                    IChatComponent component = IChatComponent.Serializer.jsonToComponent(template);
                    event.player.addChatComponentMessage(component);
                }
            }

        }
    }
}
