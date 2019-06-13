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
import de.teamlapen.vampirism.tileentity.TileTotem;
import de.teamlapen.vampirism.util.DaySleepHelper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.ModWorldEventListener;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.Village;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Handles all events used in central parts of the mod
 */
public class ModEventHandler {

    private final static Logger LOGGER = LogManager.getLogger(ModEventHandler.class);

    @SubscribeEvent
    public void onAttachCapabilitiesVillage(AttachCapabilitiesEvent<Village> event) {
        event.addCapability(REFERENCE.VAMPIRISM_VILLAGE_KEY_NEW, VampirismVillage.createNewCapability(event.getObject()));

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!UtilLib.isSameInstanceAsServer()) {
            VampirismEntityRegistry.getBiteableEntryManager().initDynamic();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (!UtilLib.isSameInstanceAsServer()) {
            Configs.onDisconnectedFromServer();
            VampirismEntityRegistry.getBiteableEntryManager().resetDynamic();
        }
    }

    @SubscribeEvent
    public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equalsIgnoreCase(REFERENCE.MODID)) {
            LOGGER.info("Configuration (%s) changed", e.getConfigID());
            Configs.onConfigurationChanged();
            Balance.onConfigurationChanged();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onInitMapGen(InitMapGenEvent event) {
        if (event.getType().equals(InitMapGenEvent.EventType.VILLAGE) && Configs.village_modify) {
            if (event.getNewGen() != event.getOriginalGen()) {
                LOGGER.warn("VillageGen", "The village map generator was overwritten by another mod. There might be a problem! \n The new generator class is " + event.getNewGen().getClass().getCanonicalName());
            }
            ModVillages.modifyVillageSize(event.getNewGen());
        }
    }


    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        VersionChecker.VersionInfo versionInfo = VampirismMod.instance.getVersionInfo();
        if (!versionInfo.isChecked()) LOGGER.warn("Version check is not finished yet");

        boolean isAdminLikePlayer = !ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(event.getPlayer());

        if (!Configs.disable_versionCheck && versionInfo.isNewVersionAvailable()) {
            if (isAdminLikePlayer || event.getPlayer().getRNG().nextInt(5) == 0) {
                if (event.getPlayer().getRNG().nextInt(4) == 0) {
                    VersionChecker.Version newVersion = versionInfo.getNewVersion();
                    //Inspired by @Vazikii's useful message
                    event.getPlayer().sendMessage(new TextComponentTranslation("text.vampirism.outdated", versionInfo.getCurrentVersion().name, newVersion.name));
                    String template = UtilLib.translate("text.vampirism.update_message");
                    template = template.replaceAll("@download@", newVersion.getUrl() == null ? versionInfo.getHomePage() : newVersion.getUrl()).replaceAll("@forum@", versionInfo.getHomePage());
                    ITextComponent component = ITextComponent.Serializer.fromJson(template);
                    event.getPlayer().sendMessage(component);
                }
            }

        }
        if (isAdminLikePlayer) {
            List<String> mods = IntegrationsNotifier.shouldNotifyAboutIntegrations();
            if (!mods.isEmpty()) {
                event.getPlayer().sendMessage(new TextComponentTranslation("text.vampirism.integrations_available.first"));
                event.getPlayer().sendMessage(new TextComponentString(TextFormatting.BLUE + TextFormatting.ITALIC.toString() + org.apache.commons.lang3.StringUtils.join(mods, ", ") + TextFormatting.RESET));
                String template = UtilLib.translate("text.vampirism.integrations_available.second");
                template = template.replaceAll("@download@", REFERENCE.INTEGRATIONS_LINK);
                event.getPlayer().sendMessage(ITextComponent.Serializer.fromJson(template));
            }

        }
        if (Configs.updated_vampirism) {
            if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(event.getPlayer())) {


                event.getPlayer().sendMessage(new TextComponentString("It looks like you have updated Vampirism"));
                event.getPlayer().sendMessage(new TextComponentString("Please consider resetting the balance values to the updated ones, using " + TextFormatting.DARK_GREEN + "'/vampirism resetBalance all'" + TextFormatting.RESET));
                event.getPlayer().sendMessage(new TextComponentString("For more information use " + TextFormatting.DARK_GREEN + "'/vampirism resetBalance help'" + TextFormatting.RESET));
            }
        }

        if (!Configs.disable_config_sync) {
            if (event.getPlayer() != null && (event.getPlayer() instanceof EntityPlayerMP)) {
                LOGGER.debug("Sending configuration to client (%s)", event.getPlayer());
                VampirismMod.dispatcher.sendTo(SyncConfigPacket.createSyncConfigPacket(), (EntityPlayerMP) event.getPlayer());
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
        TileTotem.clearCacheForDimension(event.getWorld().getDimension());
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.getState().getBlock().equals(Blocks.OAK_LEAVES)) {
            if (ModBiomes.vampireForest.equals(event.getWorld().getBiome(event.getPos()))) {
                EntityPlayer p = event.getHarvester();
                if (p != null && p.getRNG().nextInt(Balance.general.DROP_ORCHID_FROM_LEAVES_CHANCE) == 0) {
                    event.getDrops().add(new ItemStack(ModBlocks.vampirism_flower_orchid, 1));
                }
            }
        }
    }
}
