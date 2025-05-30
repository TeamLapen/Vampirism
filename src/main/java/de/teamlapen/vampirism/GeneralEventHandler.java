package de.teamlapen.vampirism;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.data.ServerSkillTreeData;
import de.teamlapen.vampirism.network.ClientboundRecipesPacket;
import de.teamlapen.vampirism.network.ClientboundSkillTreePacket;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles all events used in central parts of the mod
 */
public class GeneralEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        boolean isAdminLikePlayer = !ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(player);

        if (isAdminLikePlayer && player instanceof ServerPlayer serverPlayer) {
            //TODO 1.19
//            List<String> mods = IntegrationsNotifier.shouldNotifyAboutIntegrations();
//            if (!mods.isEmpty()) {
//                player.sendMessage(Component.translatable("text.vampirism.integrations_available.first"), Util.NIL_UUID);
//                player.sendMessage(Component.literal(ChatFormatting.BLUE + ChatFormatting.ITALIC.toString() + org.apache.commons.lang3.StringUtils.join(mods, ", ") + ChatFormatting.RESET), Util.NIL_UUID);
//                player.sendMessage(Component.translatable("text.vampirism.integrations_available.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, REFERENCE.INTEGRATIONS_LINK)).setUnderlined(true)), Util.NIL_UUID);
//            }

            if (!ModList.get().isLoaded("guideapi_vp")) {
                if (VampirismConfig.SERVER.infoAboutGuideAPI.get()) {
                    serverPlayer.sendSystemMessage(Component.translatable("text.vampirism.guideapi_available.first"));
                    serverPlayer.sendSystemMessage(Component.translatable("text.vampirism.guideapi_available.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, REFERENCE.GUIDEAPI_LINK)).withUnderlined(true)));

                    VampirismConfig.SERVER.infoAboutGuideAPI.set(false);
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
        ClientboundSkillTreePacket skillTrees = ClientboundSkillTreePacket.of(ServerSkillTreeData.instance().getConfigurations());
        if (event.getPlayer() != null) {
            event.getPlayer().connection.send(skillTrees);
        } else {
            event.getPlayerList().getPlayers().forEach(p -> p.connection.send(skillTrees));
        }
    }

    @SubscribeEvent
    public void onDataPackSync(OnDatapackSyncEvent event) {
        RecipeManager recipeManager = event.getPlayer().serverLevel().recipeAccess();
        List<RecipeHolder<?>> modRecipes = Stream.of(ModRecipes.ALCHEMICAL_CAULDRON_TYPE, ModRecipes.ALCHEMICAL_TABLE_TYPE, ModRecipes.WEAPONTABLE_CRAFTING_TYPE).map(DeferredHolder::get).flatMap(x -> recipeManager.recipeMap().byType((RecipeType<Recipe<RecipeInput>>)x).stream()).collect(Collectors.toUnmodifiableList());
        event.getPlayer().connection.send(new ClientboundRecipesPacket(modRecipes));
    }
}
