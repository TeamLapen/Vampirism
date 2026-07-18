package de.teamlapen.vampirism.common.network.packets.server;

import de.teamlapen.faction.common.world.entities.appearance.AppearanceKey;
import de.teamlapen.faction.common.world.entities.appearance.AppearancePacket;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ServerboundAppearancePacket(int entityId, AppearancePacket data) implements CustomPacketPayload {

    public static final Type<ServerboundAppearancePacket> TYPE = new Type<>(VIdentifier.mod("appearance"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundAppearancePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundAppearancePacket::entityId,
            AppearancePacket.STREAM_CODEC, ServerboundAppearancePacket::data,
            ServerboundAppearancePacket::new
    );

    public <T> ServerboundAppearancePacket(int entityId, AppearanceKey<T> name, T data) {
        this(entityId, new AppearancePacket(Map.of(name, data)));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
