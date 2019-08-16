package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.network.SkillTreePacket;
import de.teamlapen.vampirism.tileentity.TotemTile;
import de.teamlapen.vampirism.util.DaySleepHelper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Handles all events used in central parts of the mod
 */
public class ModEventHandler {

    private final static Logger LOGGER = LogManager.getLogger(ModEventHandler.class);

//  TODO 1.14 wait for https://github.com/MinecraftForge/MinecraftForge/issues/5536 or find a different solution

//    @OnlyIn(Dist.CLIENT)
//    @SubscribeEvent
//    public void onClientConnected(NetworkEvent.ClientConnectedToServerEvent event) {
//        if (!UtilLib.isSameInstanceAsServer()) {
//            VampirismEntityRegistry.getBiteableEntryManager().initDynamic();
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @SubscribeEvent
//    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
//        if (!UtilLib.isSameInstanceAsServer()) {
//            Configs.onDisconnectedFromServer();
//            VampirismEntityRegistry.getBiteableEntryManager().resetDynamic();
//        }
//    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void on(WorldEvent.Load event) {
        ChunkGenerator generator = event.getWorld().getChunkProvider().getChunkGenerator();
        if (generator instanceof OverworldChunkGenerator) {
            GenerationSettings settings = ((OverworldChunkGenerator) generator).getSettings();
            ModWorld.modifyVillageSize(settings);
        }
    }

    @SubscribeEvent
    public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equalsIgnoreCase(REFERENCE.MODID)) {
            LOGGER.info("Configuration ({}) changed", e.getConfigID());
            Balance.onConfigurationChanged(); //TODO 1.14 see if this still exist after Forge readds config GUI. And if this is necessary or if the events inside VampirismConfig are sufficient
        }
    }


    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.getState().getBlock().equals(Blocks.OAK_LEAVES)) {
            if (ModBiomes.vampire_forest.equals(event.getWorld().getBiome(event.getPos()))) {
                PlayerEntity p = event.getHarvester();
                if (p != null && p.getRNG().nextInt(Balance.general.DROP_ORCHID_FROM_LEAVES_CHANCE) == 0) {
                    event.getDrops().add(new ItemStack(ModBlocks.vampirism_flower_vampire_orchid, 1));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        VersionChecker.VersionInfo versionInfo = VampirismMod.instance.getVersionInfo();
        if (!versionInfo.isChecked()) LOGGER.warn("Version check is not finished yet");

        boolean isAdminLikePlayer = !ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(event.getPlayer());

        if (VampirismConfig.COMMON.versionCheck.get() && versionInfo.isNewVersionAvailable()) {
            if (isAdminLikePlayer || event.getPlayer().getRNG().nextInt(5) == 0) {
                if (event.getPlayer().getRNG().nextInt(4) == 0) {
                    VersionChecker.Version newVersion = versionInfo.getNewVersion();
                    //Inspired by @Vazikii's useful message
                    event.getPlayer().sendMessage(new TranslationTextComponent("text.vampirism.outdated", versionInfo.getCurrentVersion().name, newVersion.name));
                    String template = UtilLib.translate("text.vampirism.update_message");
                    template = template.replaceAll("@download@", newVersion.getUrl() == null ? versionInfo.getHomePage() : newVersion.getUrl()).replaceAll("@forum@", versionInfo.getHomePage());
                    ITextComponent component = ITextComponent.Serializer.fromJson(template);
                    event.getPlayer().sendMessage(component);
                }
            }

        }
        if (isAdminLikePlayer) {
            List<String> mods = Collections.emptyList();// TODO 1.14 IntegrationsNotifier.shouldNotifyAboutIntegrations();
            if (!mods.isEmpty()) {
                event.getPlayer().sendMessage(new TranslationTextComponent("text.vampirism.integrations_available.first"));
                event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.BLUE + TextFormatting.ITALIC.toString() + org.apache.commons.lang3.StringUtils.join(mods, ", ") + TextFormatting.RESET));
                String template = UtilLib.translate("text.vampirism.integrations_available.second");
                template = template.replaceAll("@download@", REFERENCE.INTEGRATIONS_LINK);
                event.getPlayer().sendMessage(ITextComponent.Serializer.fromJson(template));
            }

        }
        VampirismMod.dispatcher.sendTo(new SkillTreePacket(VampirismMod.proxy.getSkillTree(false).getCopy()), (ServerPlayerEntity) event.getPlayer());


//        if (Configs.updated_vampirism) { TODO 1.14 Balance
//            if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(event.getPlayer())) {
//
//
//                event.getPlayer().sendMessage(new StringTextComponent("It looks like you have updated Vampirism"));
//                event.getPlayer().sendMessage(new StringTextComponent("Please consider resetting the balance values to the updated ones, using " + TextFormatting.DARK_GREEN + "'/vampirism resetBalance all'" + TextFormatting.RESET));
//                event.getPlayer().sendMessage(new StringTextComponent("For more information use " + TextFormatting.DARK_GREEN + "'/vampirism resetBalance help'" + TextFormatting.RESET));
//            }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        VampirismAPI.getGarlicChunkHandler(event.getWorld().getWorld()).clear();
        TotemTile.clearCacheForDimension(event.getWorld().getDimension());
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) {
            //VampirismVillageHelper.tick(event.world);
            if (event.world.getGameTime() % 16 == 0) {
                DaySleepHelper.checkSleepWorld(event.world);
            }
        }
    }
}
