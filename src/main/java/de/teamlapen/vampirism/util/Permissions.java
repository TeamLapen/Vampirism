package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

public class Permissions {
    public static final PermissionNode<Boolean> GENERAL_CHECK = new PermissionNode<>(REFERENCE.MODID, "check", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true));
    public static final PermissionNode<Boolean> FEED = new PermissionNode<>(REFERENCE.MODID, "bite.feed", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true));
    public static final PermissionNode<Boolean> FEED_PLAYER = new PermissionNode<>(REFERENCE.MODID, "bite.feed.player", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true));
    public static final PermissionNode<Boolean> INFECT_PLAYER = new PermissionNode<>(REFERENCE.MODID, "infect.player", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true));
    public static final PermissionNode<Boolean> ACTION = new PermissionNode<>(REFERENCE.MODID, "action", PermissionTypes.BOOLEAN, (player, playerUUID, context) -> true);
    @SuppressWarnings("rawtypes")
    public static final PermissionDynamicContextKey<IAction> ACTION_CONTEXT = new PermissionDynamicContextKey<>(IAction.class, "action", action -> RegUtil.id(action).toString());


    @SubscribeEvent
    public static void registerNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(GENERAL_CHECK, FEED, FEED_PLAYER, INFECT_PLAYER, ACTION);
    }

    public static boolean isPvpEnabled(Player player) {
        if (!player.getCommandSenderWorld().isClientSide) {
            return ServerLifecycleHooks.getCurrentServer().isPvpAllowed();
        }
        return true;
    }


}
