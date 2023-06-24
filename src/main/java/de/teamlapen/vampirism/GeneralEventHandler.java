package de.teamlapen.vampirism;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.ClientboundBloodValuePacket;
import de.teamlapen.vampirism.network.ClientboundSkillTreePacket;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.world.MinionWorldData;
import de.teamlapen.vampirism.world.VampirismWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * Handles all events used in central parts of the mod
 */
public class GeneralEventHandler {

    private final static Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onAttachCapabilityWorld(@NotNull AttachCapabilitiesEvent<Level> event) {
        event.addCapability(REFERENCE.WORLD_CAP_KEY, VampirismWorld.createNewCapability(event.getObject()));
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        boolean isAdminLikePlayer = !ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(player);

        if (isAdminLikePlayer) {
            //TODO 1.19
//            List<String> mods = IntegrationsNotifier.shouldNotifyAboutIntegrations();
//            if (!mods.isEmpty()) {
//                player.sendMessage(Component.translatable("text.vampirism.integrations_available.first"), Util.NIL_UUID);
//                player.sendMessage(Component.literal(ChatFormatting.BLUE + ChatFormatting.ITALIC.toString() + org.apache.commons.lang3.StringUtils.join(mods, ", ") + ChatFormatting.RESET), Util.NIL_UUID);
//                player.sendMessage(Component.translatable("text.vampirism.integrations_available.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, REFERENCE.INTEGRATIONS_LINK)).setUnderlined(true)), Util.NIL_UUID);
//            }

            if (!ModList.get().isLoaded("guideapi_vp")) {
                if (VampirismConfig.SERVER.infoAboutGuideAPI.get()) {
                    player.sendSystemMessage(Component.translatable("text.vampirism.guideapi_available.first"));
                    player.sendSystemMessage(Component.translatable("text.vampirism.guideapi_available.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, REFERENCE.GUIDEAPI_LINK)).withUnderlined(true)));

                    VampirismConfig.SERVER.infoAboutGuideAPI.set(false);
                }
            }
        }
//        if (event.getPlayer().getRNG().nextInt(3) == 0) {
//            event.getPlayer().sendStatusMessage(new StringTextComponent("You are playing an alpha version of Vampirism for 1.16, some things might not work yet. Please report any issues except for:").mergeStyle(TextFormatting.RED), false);
//        }

        if (player instanceof ServerPlayer serverPlayer) {
            VampirismMod.dispatcher.sendTo(new ClientboundSkillTreePacket(VampirismMod.proxy.getSkillTree(false).getCopy()), serverPlayer);
        }

        @SuppressWarnings("unchecked")
        Map<ResourceLocation, Float>[] bloodValues = (Map<ResourceLocation, Float>[]) Array.newInstance(Map.class, 3);
        bloodValues[0] = BloodConversionRegistry.getEntityConversions();
        bloodValues[1] = BloodConversionRegistry.getItemConversions();
        bloodValues[2] = BloodConversionRegistry.getFluidConversions();

        if (player instanceof ServerPlayer serverPlayer) {
            VampirismMod.dispatcher.sendTo(new ClientboundBloodValuePacket(bloodValues), serverPlayer);
        }
        FactionPlayerHandler.getOpt(player).ifPresent(FactionPlayerHandler::onPlayerLoggedIn);

        if (player instanceof ServerPlayer && !PermissionAPI.getPermission((ServerPlayer) player, Permissions.GENERAL_CHECK)) {
            player.sendSystemMessage(Component.literal("[" + ChatFormatting.DARK_PURPLE + "Vampirism" + ChatFormatting.RESET + "] It seems like the permission plugin used is not properly set up. Make sure all players have 'vampirism.*' for the mod to work (or at least '" + Permissions.GENERAL_CHECK.getNodeName() + "' to suppress this warning)."));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.@NotNull ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        MinionWorldData.getData(ServerLifecycleHooks.getCurrentServer()).tick();

    }


    @SubscribeEvent
    public void onWorldUnload(LevelEvent.@NotNull Unload event) {
        if (event.getLevel() instanceof Level level) {
            VampirismWorld.getOpt(level).ifPresent(VampirismWorld::clearCaches);
        }
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        int missing = ModLootTables.checkAndResetInsertedAll();
        if (missing > 0) {
            LOGGER.warn("LootTables Failed to inject {} loottables", missing);
        }
    }
}
