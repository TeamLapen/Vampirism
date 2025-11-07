package de.teamlapen.vampirism.server;

import de.teamlapen.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundRecipesPacket;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundSkillTreePacket;
import de.teamlapen.vampirism.common.util.Permissions;
import de.teamlapen.vampirism.common.world.saved.MinionWorldData;
import de.teamlapen.vampirism.server.data.ServerSkillTreeData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber
public class ServerEventHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
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
    public static void onServerTick(ServerTickEvent.Pre event) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            MinionWorldData.getData(ServerLifecycleHooks.getCurrentServer()).tick();
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        sendSkillTree(event);
        sendRecipes(event);
    }

    private static void sendSkillTree(OnDatapackSyncEvent event) {
        ClientboundSkillTreePacket skillTrees = ClientboundSkillTreePacket.of(ServerSkillTreeData.instance().getConfigurations());
        if (event.getPlayer() != null) {
            event.getPlayer().connection.send(skillTrees);
        } else {
            event.getPlayerList().getPlayers().forEach(p -> p.connection.send(skillTrees));
        }
    }

    private static void sendRecipes(OnDatapackSyncEvent event) {
        RecipeManager recipeManager = event.getPlayer().level().recipeAccess();
        List<RecipeHolder<?>> modRecipes = Stream.of(ModRecipes.ALCHEMICAL_CAULDRON_TYPE, ModRecipes.ALCHEMICAL_TABLE_TYPE, ModRecipes.WEAPONTABLE_CRAFTING_TYPE).map(DeferredHolder::get).flatMap(x -> recipeManager.recipeMap().byType((RecipeType<Recipe<RecipeInput>>)x).stream()).collect(Collectors.toUnmodifiableList());
        event.getPlayer().connection.send(new ClientboundRecipesPacket(modRecipes));
    }
}
