package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class Permissions {
    public static final PermissionDynamicContextKey<IAction<?>> ACTION_CONTEXT = new PermissionDynamicContextKey<>((Class<IAction<?>>) (Object) IAction.class, "action", action -> RegUtil.id(action).toString());

    public static final PermissionNode<Boolean> GENERAL_CHECK = new PermissionNode<>(REFERENCE.MODID, "check", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true));
    public static final Permission FEED = create(new PermissionNode<>(REFERENCE.MODID, "bite.feed", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true)));
    public static final Permission FEED_PLAYER = create(new PermissionNode<>(REFERENCE.MODID, "bite.feed.player", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true)));
    public static final Permission INFECT_PLAYER = create(new PermissionNode<>(REFERENCE.MODID, "infect.player", PermissionTypes.BOOLEAN, ((player, playerUUID, context) -> true)));
    public static final ContextPermission<IAction<?>> ACTION = new ContextPermission<>(new PermissionNode<>(REFERENCE.MODID, "action", PermissionTypes.BOOLEAN, (player, playerUUID, context) -> true), ACTION_CONTEXT);


    @SubscribeEvent
    public static void registerNodes(PermissionGatherEvent.@NotNull Nodes event) {
        event.addNodes(GENERAL_CHECK, FEED.node, FEED_PLAYER.node, INFECT_PLAYER.node, ACTION.getNode());
    }

    public static boolean isPvpEnabled(@NotNull Player player) {
        if (!player.getCommandSenderWorld().isClientSide) {
            return ServerLifecycleHooks.getCurrentServer().isPvpAllowed();
        }
        return true;
    }

    private static Permission create(PermissionNode<Boolean> node) {
        return new Permission(node);
    }

    public static boolean isSetupCorrectly(ServerPlayer player) {
        return !VampirismConfig.SERVER.usePermissions.get() || PermissionAPI.getPermission(player, GENERAL_CHECK);
    }

    public record Permission(PermissionNode<Boolean> node) {
        public boolean isAllowed(ServerPlayer player) {
            return !VampirismConfig.SERVER.usePermissions.get() || PermissionAPI.getPermission(player, this.node);
        }

        public boolean isDisallowed(ServerPlayer player) {
            return !isAllowed(player);
        }
    }

    public static class ContextPermission<T>  {

        private final PermissionNode<Boolean> node;
        private final PermissionDynamicContextKey<T> context;

        public ContextPermission(PermissionNode<Boolean> node, PermissionDynamicContextKey<T> context) {
            this.node = node;
            this.context = context;
        }

        public boolean isAllowed(ServerPlayer player, T context) {
            return !VampirismConfig.SERVER.usePermissions.get() || PermissionAPI.getPermission(player, this.node, this.context.createContext(context));
        }

        @SafeVarargs
        public final boolean isAllowed(ServerPlayer player, T... context) {
            return !VampirismConfig.SERVER.usePermissions.get() || PermissionAPI.getPermission(player, this.node, Arrays.stream(context).map(this.context::createContext).toArray(PermissionDynamicContext[]::new));
        }

        public boolean isDisallowed(ServerPlayer player, T context) {
            return !isAllowed(player, context);
        }

        @SafeVarargs
        public final boolean isDisallowed(ServerPlayer player, T... context) {
            return !isAllowed(player, context);
        }

        public PermissionNode<Boolean> getNode() {
            return node;
        }
    }


}
