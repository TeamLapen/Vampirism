package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record ClientboundUpdateDimensionsPacket(Set<ResourceKey<Level>> keys, boolean add) implements CustomPacketPayload {

    public static final Type<ClientboundUpdateDimensionsPacket> TYPE = new Type<>(VIdentifier.mod("update_dimensions"));
    public static final StreamCodec<ByteBuf, ClientboundUpdateDimensionsPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()).map(Set::copyOf, List::copyOf), ClientboundUpdateDimensionsPacket::keys,
            ByteBufCodecs.BOOL, ClientboundUpdateDimensionsPacket::add,
            ClientboundUpdateDimensionsPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
