package de.teamlapen.vampirism;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.modcompat.IntegrationsNotifier;
import de.teamlapen.vampirism.network.BloodValuePacket;
import de.teamlapen.vampirism.network.SkillTreePacket;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.world.MinionWorldData;
import de.teamlapen.vampirism.world.VampirismWorld;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Handles all events used in central parts of the mod
 */
public class GeneralEventHandler {

    private final static Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onAttachCapabilityWorld(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(REFERENCE.WORLD_CAP_KEY, VampirismWorld.createNewCapability(event.getObject()));
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        VersionChecker.VersionInfo versionInfo = VampirismMod.instance.getVersionInfo();
        if (!versionInfo.isChecked()) LOGGER.warn("Version check is not finished yet");

        boolean isAdminLikePlayer = !ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(event.getPlayer());

        if (VampirismConfig.COMMON.versionCheck.get() && versionInfo.isNewVersionAvailable()) {
            if (isAdminLikePlayer || event.getPlayer().getRandom().nextInt(5) == 0) {
                if (event.getPlayer().getRandom().nextInt(4) == 0) {
                    VersionChecker.Version newVersion = versionInfo.getNewVersion();
                    event.getPlayer().sendMessage(new TranslatableComponent("text.vampirism.outdated", versionInfo.getCurrentVersion().name, newVersion.name), Util.NIL_UUID);
                    Component download = new TranslatableComponent("text.vampirism.update_message.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, newVersion.getUrl() == null ? versionInfo.getHomePage() : newVersion.getUrl())).setUnderlined(true).applyFormat(ChatFormatting.BLUE));
                    Component changelog = new TranslatableComponent("text.vampirism.update_message.changelog").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vampirism changelog")).setUnderlined(true));
                    Component modpage = new TranslatableComponent("text.vampirism.update_message.modpage").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, versionInfo.getHomePage())).setUnderlined(true).applyFormat(ChatFormatting.BLUE));
                    event.getPlayer().sendMessage(new TextComponent("").append(download).append(new TextComponent(" ")).append(changelog).append(new TextComponent(" ")).append(modpage), Util.NIL_UUID);
                }
            }
        }
        if (isAdminLikePlayer) {
            List<String> mods = IntegrationsNotifier.shouldNotifyAboutIntegrations();
            if (!mods.isEmpty()) {
                event.getPlayer().sendMessage(new TranslatableComponent("text.vampirism.integrations_available.first"), Util.NIL_UUID);
                event.getPlayer().sendMessage(new TextComponent(ChatFormatting.BLUE + ChatFormatting.ITALIC.toString() + org.apache.commons.lang3.StringUtils.join(mods, ", ") + ChatFormatting.RESET), Util.NIL_UUID);
                event.getPlayer().sendMessage(new TranslatableComponent("text.vampirism.integrations_available.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, REFERENCE.INTEGRATIONS_LINK)).setUnderlined(true)), Util.NIL_UUID);
            }

            if (!ModList.get().isLoaded("guideapi-vp")) {
                if (VampirismConfig.SERVER.infoAboutGuideAPI.get()) {
                    event.getPlayer().sendMessage(new TranslatableComponent("text.vampirism.guideapi_available.first"), Util.NIL_UUID);
                    event.getPlayer().sendMessage(new TranslatableComponent("text.vampirism.guideapi_available.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, REFERENCE.GUIDEAPI_LINK)).setUnderlined(true)), Util.NIL_UUID);

                    VampirismConfig.SERVER.infoAboutGuideAPI.set(false);
                }
            }
        }
//        if (event.getPlayer().getRNG().nextInt(3) == 0) {
//            event.getPlayer().sendStatusMessage(new StringTextComponent("You are playing an alpha version of Vampirism for 1.16, some things might not work yet. Please report any issues except for:").mergeStyle(TextFormatting.RED), false);
//        }

        VampirismMod.dispatcher.sendTo(new SkillTreePacket(VampirismMod.proxy.getSkillTree(false).getCopy()), (ServerPlayer) event.getPlayer());

        @SuppressWarnings("unchecked")
        Pair<Map<ResourceLocation, Integer>, Integer>[] bloodValues = (Pair<Map<ResourceLocation, Integer>, Integer>[]) Array.newInstance(Pair.class, 3);
        bloodValues[0] = new Pair<>(((VampirismEntityRegistry) VampirismAPI.entityRegistry()).getBloodValues(), ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).getBloodMultiplier());
        bloodValues[1] = new Pair<>(BloodConversionRegistry.getItemValues(), BloodConversionRegistry.getItemMultiplier());
        bloodValues[2] = new Pair<>(BloodConversionRegistry.getFluidValues(), BloodConversionRegistry.getFluidDivider());

        VampirismMod.dispatcher.sendTo(new BloodValuePacket(bloodValues), (ServerPlayer) event.getPlayer());
        FactionPlayerHandler.getOpt(event.getPlayer()).ifPresent(FactionPlayerHandler::onPlayerLoggedIn);

        if (!PermissionAPI.hasPermission(event.getPlayer(), Permissions.VAMPIRISM)) {
            event.getPlayer().sendMessage(new TextComponent("[" + ChatFormatting.DARK_PURPLE + "Vampirism" + ChatFormatting.RESET + "] It seems like the permission plugin used is not properly set up. Make sure all players have 'vampirism.*' for the mod to work (or at least '" + Permissions.VAMPIRISM + "' to suppress this warning)."), Util.NIL_UUID);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        MinionWorldData.getData(ServerLifecycleHooks.getCurrentServer()).tick();

    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel) {
            ServerLevel w = (ServerLevel) event.getWorld();
            ServerChunkCache p = w.getChunkSource();
            ModFeatures.checkWorldStructureSeparation(w.dimension(), p.getGenerator() instanceof FlatLevelSource, p.getGenerator().getSettings());

        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof Level) {
            VampirismWorld.getOpt((Level) event.getWorld()).ifPresent(VampirismWorld::clear);
        }
    }
}
