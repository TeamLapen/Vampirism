package de.teamlapen.faction.common.network.packets.client;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ClientboundEventPacket(int entityId, Identifier eventId) implements CustomPacketPayload {

    public ClientboundEventPacket(Entity entity, Identifier eventId) {
        this(entity.getId(), eventId);
    }

    public static final Type<ClientboundEventPacket> TYPE = new Type<>(FIdentifier.mod("event"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundEventPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundEventPacket::entityId,
            Identifier.STREAM_CODEC, ClientboundEventPacket::eventId,
            ClientboundEventPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToTracking(@NotNull Entity entity, Identifier eventId) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().sendToTrackingPlayers(entity, new ClientboundEventPacket(entity, eventId));
        }
    }
}
