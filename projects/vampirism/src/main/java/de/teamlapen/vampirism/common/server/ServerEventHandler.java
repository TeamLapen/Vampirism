package de.teamlapen.vampirism.common.server;

import de.teamlapen.factions.common.factions.minions.MinionWorldData;
import de.teamlapen.factions.common.factions.skills.ClientboundSkillTreePacket;
import de.teamlapen.factions.common.factions.skills.ServerSkillTreeData;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.util.Permissions;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.structures.VanillaStructureModifications;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ServerEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        boolean isAdminLikePlayer = !ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(player);

        if (isAdminLikePlayer && player instanceof ServerPlayer serverPlayer) {
            if (!ModList.get().isLoaded("guideapi_vp")) {
                if (ModConfig.SERVER.infoAboutGuideAPI.get()) {
                    serverPlayer.sendSystemMessage(Component.translatable("text.vampirism.guideapi_available.first"));
                    serverPlayer.sendSystemMessage(Component.translatable("text.vampirism.guideapi_available.download").withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(REFERENCE.GUIDEAPI_LINK)).withUnderlined(true)));

                    ModConfig.SERVER.infoAboutGuideAPI.set(false);
                }
            }
        }

        if (player instanceof ServerPlayer serverPlayer && !Permissions.isSetupCorrectly(serverPlayer)) {
            serverPlayer.sendSystemMessage(Component.literal("[" + ChatFormatting.DARK_PURPLE + "Vampirism" + ChatFormatting.RESET + "] It seems like the permission plugin used is not properly set up. Make sure all players have 'vampirism.*' for the mod to work (or at least '" + Permissions.GENERAL_CHECK.getNodeName() + "' to suppress this warning)."));
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Pre event) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            MinionWorldData.getData(ServerLifecycleHooks.getCurrentServer()).tick();
        }
    }

    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        sendSkillTree(event);
    }

    private void sendSkillTree(OnDatapackSyncEvent event) {
        ClientboundSkillTreePacket skillTrees = ClientboundSkillTreePacket.of(ServerSkillTreeData.instance().getConfigurations());
        if (event.getPlayer() != null) {
            event.getPlayer().connection.send(skillTrees);
        } else {
            event.getPlayerList().getPlayers().forEach(p -> p.connection.send(skillTrees));
        }
    }

    @SubscribeEvent
    private void onServerStarting(ServerAboutToStartEvent event) {
        VanillaStructureModifications.addVillageStructures(event.getServer().registryAccess());
        VampirismMod.services().sunDamageRegistry().initServer(event.getServer().registryAccess());
    }

    @SubscribeEvent
    private void onServerStopped(ServerStoppedEvent event) {
        VampirismMod.services().sunDamageRegistry().removeServer();
    }
}
