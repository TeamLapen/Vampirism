package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.*;


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
        return FactionPlayerHandler.getOpt(player).flatMap(fp -> {
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
        });
    }

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "request_minion_select");
    public static final Codec<ClientboundRequestMinionSelectPacket> CODEC = RecordCodecBuilder.create(inst
            -> inst.group(
                    StringRepresentable.fromEnum(Action::values).fieldOf("action").forGetter(ClientboundRequestMinionSelectPacket::action),
                    Codec.pair(Codec.INT, ComponentSerialization.CODEC).listOf().fieldOf("minions").forGetter(ClientboundRequestMinionSelectPacket::minions)
    ).apply(inst, ClientboundRequestMinionSelectPacket::new));

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }


    public enum Action implements StringRepresentable {
        CALL("call");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
