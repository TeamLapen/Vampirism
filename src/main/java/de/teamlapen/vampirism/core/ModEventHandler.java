package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.modcompat.IntegrationsNotifier;
import de.teamlapen.vampirism.network.SyncConfigPacket;
import de.teamlapen.vampirism.util.DaySleepHelper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.ModWorldEventListener;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.Village;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Handles all events used in central parts of the mod
 */
public class ModEventHandler {
    private final static String TAG = "EventHandler";

    @SubscribeEvent
    public void onAttachCapabilitiesVillage(AttachCapabilitiesEvent<Village> event) {
        event.addCapability(REFERENCE.VAMPIRISM_VILLAGE_KEY, VampirismVillage.createNewCapability(event.getObject()));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (!UtilLib.isSameInstanceAsServer()) {
            Configs.onDisconnectedFromServer();
            VampirismEntityRegistry.getBiteableEntryManager().resetDynamic();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!UtilLib.isSameInstanceAsServer()) {
            VampirismEntityRegistry.getBiteableEntryManager().initDynamic();
        }
    }

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
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        VersionChecker.VersionInfo versionInfo = VampirismMod.instance.getVersionInfo();
        if (!versionInfo.isChecked()) VampirismMod.log.w(TAG, "Version check is not finished yet");

        boolean isAdminLikePlayer = !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() || UtilLib.isPlayerOp(event.player);

        if (!Configs.disable_versionCheck && versionInfo.isNewVersionAvailable()) {
            if (isAdminLikePlayer || event.player.getRNG().nextInt(5) == 0) {
                if (event.player.getRNG().nextInt(4) == 0) {
                    VersionChecker.Version newVersion = versionInfo.getNewVersion();
                    //Inspired by @Vazikii's useful message
                    event.player.sendMessage(new TextComponentTranslation("text.vampirism.outdated", versionInfo.getCurrentVersion().name, newVersion.name));
                    String template = UtilLib.translate("text.vampirism.update_message");
                    template = template.replaceAll("@download@", newVersion.getUrl() == null ? versionInfo.getHomePage() : newVersion.getUrl()).replaceAll("@forum@", versionInfo.getHomePage());
                    ITextComponent component = ITextComponent.Serializer.jsonToComponent(template);
                    event.player.sendMessage(component);
                }
            }

        }
        if (isAdminLikePlayer) {
            List<String> mods = IntegrationsNotifier.shouldNotifyAboutIntegrations();
            if (!mods.isEmpty()) {
                event.player.sendMessage(new TextComponentTranslation("text.vampirism.integrations_available.first"));
                event.player.sendMessage(new TextComponentString(TextFormatting.BLUE + TextFormatting.ITALIC.toString() + org.apache.commons.lang3.StringUtils.join(mods, ", ") + TextFormatting.RESET));
                String template = UtilLib.translate("text.vampirism.integrations_available.second");
                template = template.replaceAll("@download@", REFERENCE.INTEGRATIONS_LINK);
                event.player.sendMessage(ITextComponent.Serializer.jsonToComponent(template));
            }

        }
        if (Configs.updated_vampirism) {
            if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() || UtilLib.isPlayerOp(event.player)) {


                event.player.sendMessage(new TextComponentString("It looks like you have updated Vampirism"));
                event.player.sendMessage(new TextComponentString("Please consider resetting the balance values to the updated ones, using " + TextFormatting.DARK_GREEN + "'/vampirism resetBalance all'" + TextFormatting.RESET));
                event.player.sendMessage(new TextComponentString("For more information use " + TextFormatting.DARK_GREEN + "'/vampirism resetBalance help'" + TextFormatting.RESET));
            }
        }

        if (!Configs.disable_config_sync) {
            if (event.player != null && (event.player instanceof EntityPlayerMP)) {
                VampirismMod.log.d(TAG, "Sending configuration to client (%s)", event.player);
                VampirismMod.dispatcher.sendTo(SyncConfigPacket.createSyncConfigPacket(), (EntityPlayerMP) event.player);
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener(new ModWorldEventListener(event.getWorld().provider.getDimension()));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) {
            DaySleepHelper.checkSleepWorld(event.world);
            VampirismVillageHelper.tick(event.world);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        VampirismAPI.getGarlicChunkHandler(event.getWorld()).clear();
    }
}
