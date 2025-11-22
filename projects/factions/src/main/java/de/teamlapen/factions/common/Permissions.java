package de.teamlapen.factions.common;

import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.config.ModConfig;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Arrays;

public class Permissions {

    @SuppressWarnings("unchecked")
    public static final PermissionDynamicContextKey<IAction<?>> ACTION_CONTEXT = new PermissionDynamicContextKey<>((Class<IAction<?>>) (Object) IAction.class, "action", action -> RegUtil.id(action).toString());

    public static final PermissionNode<Boolean> GENERAL_CHECK = new PermissionNode<>(REFERENCE.MOD_ID, "check", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true));
    public static final ContextPermission<IAction<?>> ACTION = new ContextPermission<>(new PermissionNode<>(REFERENCE.MOD_ID, "action", PermissionTypes.BOOLEAN, (player, playerUUID, context) -> true), ACTION_CONTEXT);

    public static void registerNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(GENERAL_CHECK, ACTION.node());
    }

    public static boolean isPvpEnabled(Player player) {
        if (!player.level().isClientSide()) {
            return ServerLifecycleHooks.getCurrentServer().isPvpAllowed();
        }
        return true;
    }

    public record ContextPermission<T>(PermissionNode<Boolean> node, PermissionDynamicContextKey<T> context) {

        public boolean isAllowed(ServerPlayer player, T context) {
            return !ModConfig.SERVER.usePermissions.get() || PermissionAPI.getPermission(player, this.node, this.context.createContext(context));
        }

        @SafeVarargs
        public final boolean isAllowed(ServerPlayer player, T... context) {
            return !ModConfig.SERVER.usePermissions.get() || PermissionAPI.getPermission(player, this.node, Arrays.stream(context).map(this.context::createContext).toArray(PermissionDynamicContext[]::new));
        }

        public boolean isDisallowed(ServerPlayer player, T context) {
            return !isAllowed(player, context);
        }

        @SafeVarargs
        public final boolean isDisallowed(ServerPlayer player, T... context) {
            return !isAllowed(player, context);
        }
    }
}
