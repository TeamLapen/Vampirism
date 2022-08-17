package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


public record ClientboundRequestMinionSelectPacket(Action action, List<Pair<Integer, Component>> minions) implements IMessage {

    /**
     * Create a minion selection request that can be sent to the client (player).
     * It offers all available/callable minions up for selection.
     * If no minions are available, it prints a status message to the player and returns empty
     *
     * @param action The action that should be executed for the selected minion
     * @return Empty if no minions are available
     */
    public static @NotNull Optional<ClientboundRequestMinionSelectPacket> createRequestForPlayer(@NotNull ServerPlayer player, Action action) {
        return FactionPlayerHandler.getOpt(player).resolve().flatMap(fp -> {
            PlayerMinionController controller = MinionWorldData.getData(player.server).getOrCreateController(fp);
            Collection<Integer> ids = controller.getCallableMinions();
            if (ids.size() > 0) {
                List<Pair<Integer, Component>> minions = new ArrayList<>(ids.size());
                ids.forEach(id -> controller.contactMinionData(id, data -> data.getFormattedName().copy()).ifPresent(n -> minions.add(Pair.of(id, n))));
                return Optional.of(new ClientboundRequestMinionSelectPacket(action, minions));
            } else {
                ServerboundSelectMinionTaskPacket.printRecoveringMinions(player, controller.getRecoveringMinionNames());
            }
            return Optional.empty();
        });
    }

    public static void handle(final @NotNull ClientboundRequestMinionSelectPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleRequestMinionSelect(msg.action, msg.minions));
        ctx.setPacketHandled(true);
    }

    static void encode(@NotNull ClientboundRequestMinionSelectPacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeVarInt(msg.action.ordinal());
        buf.writeVarInt(msg.minions.size());
        for (Pair<Integer, Component> minion : msg.minions) {
            buf.writeVarInt(minion.getLeft());
            buf.writeComponent(minion.getRight());
        }

    }

    static @NotNull ClientboundRequestMinionSelectPacket decode(@NotNull FriendlyByteBuf buf) {
        Action a = Action.values()[buf.readVarInt()];
        int count = buf.readVarInt();
        List<Pair<Integer, Component>> minions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            minions.add(Pair.of(buf.readVarInt(), buf.readComponent()));
        }
        return new ClientboundRequestMinionSelectPacket(a, minions);
    }

    public enum Action {
        CALL
    }
}
