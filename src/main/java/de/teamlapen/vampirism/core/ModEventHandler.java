package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.potion.FakeNightVisionPotion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

/**
 * Handles all events used in central parts of the mod
 */
public class ModEventHandler {
    private final static String TAG = "EventHandler";

    @SubscribeEvent
    public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equalsIgnoreCase(REFERENCE.MODID)) {
            VampirismMod.log.i(TAG, "Configuration (%s) changed", e.getConfigID());
            Configs.onConfigurationChanged();
            Balance.onConfigurationChanged();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onInitMapGen(InitMapGenEvent event) {
        if (event.getType().equals(InitMapGenEvent.EventType.VILLAGE) && Configs.village_modify) {
            if (event.getNewGen() != event.getOriginalGen()) {
                VampirismMod.log.w("VillageGen", "The village map generator was overwritten by another mod. There might be a problem! \n The new generator class is " + event.getNewGen().getClass().getCanonicalName());
            }
            ModVillages.modifyVillageSize(event.getNewGen());
        }
    }

    @SubscribeEvent
    public void onPlayerJoinedWorld(EntityJoinWorldEvent entityJoinWorldEvent) {
        if ((entityJoinWorldEvent.getEntity() instanceof EntityPlayer)) {
            if (VampirismMod.proxy.isClientPlayerNull() || VampirismMod.proxy.isPlayerThePlayer((EntityPlayer) entityJoinWorldEvent.getEntity())) {
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
            if (!FMLServerHandler.instance().getServer().isDedicatedServer() || FMLServerHandler.instance().getServer().getPlayerList().getOppedPlayers().getEntry(event.player.getGameProfile()) != null || event.player.getRNG().nextInt(5) == 0) {
                if (event.player.getRNG().nextInt(4) == 0) {
                    VersionChecker.Version newVersion = versionInfo.getNewVersion();
                    //Inspired by @Vazikii's useful message
                    event.player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.outdated", versionInfo.getCurrentVersion().name, newVersion.name));
                    String template = I18n.translateToLocal("text.vampirism.update_message");
                    template = template.replaceAll("@download@", newVersion.getUrl() == null ? versionInfo.getHomePage() : newVersion.getUrl()).replaceAll("@forum@", versionInfo.getHomePage());
                    ITextComponent component = ITextComponent.Serializer.jsonToComponent(template);
                    event.player.addChatComponentMessage(component);
                }
            }

        }
    }
}
