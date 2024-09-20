package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.util.ByteBufferCodecUtil;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public record ClientboundRequestMinionSelectPacket(Action action, List<Pair<Integer, Component>> minions) implements CustomPacketPayload {

    /**
     * Create a minion selection request that can be sent to the client (player).
     * It offers all available/callable minions up for selection.
     * If no minions are available, it prints a status message to the player and returns empty
     *
     * @param action The action that should be executed for the selected minion
     * @return Empty if no minions are available
     */
    public static @NotNull Optional<ClientboundRequestMinionSelectPacket> createRequestForPlayer(@NotNull ServerPlayer player, Action action) {
        FactionPlayerHandler fp = FactionPlayerHandler.get(player);
        PlayerMinionController controller = MinionWorldData.getData(player.server).getOrCreateController(fp);
        Collection<Integer> ids = controller.getCallableMinions();
        if (!ids.isEmpty()) {
            List<Pair<Integer, Component>> minions = new ArrayList<>(ids.size());
            ids.forEach(id -> controller.contactMinionData(id, data -> data.getFormattedName().copy()).ifPresent(n -> minions.add(Pair.of(id, n))));
            return Optional.of(new ClientboundRequestMinionSelectPacket(action, minions));
        } else {
            ServerboundSelectMinionTaskPacket.printRecoveringMinions(player, controller.getRecoveringMinionNames());
        }
        return Optional.empty();
    }

    public static final Type<ClientboundRequestMinionSelectPacket> TYPE = new Type<>(VResourceLocation.mod("request_minion_select"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRequestMinionSelectPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(Action::valueOf, Enum::name), ClientboundRequestMinionSelectPacket::action,
            ByteBufferCodecUtil.pair(ByteBufCodecs.VAR_INT, ComponentSerialization.STREAM_CODEC).apply(ByteBufCodecs.collection(i -> new ArrayList<>())), ClientboundRequestMinionSelectPacket::minions,
            ClientboundRequestMinionSelectPacket::new
    );


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Action {
        CALL
    }
}
