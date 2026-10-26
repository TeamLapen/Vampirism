package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.world.entities.customization.CustomizationData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundCustomizationPacket(int entityId, CustomizationData data) implements CustomPacketPayload {

    public static final Type<ServerboundCustomizationPacket> TYPE = new Type<>(FIdentifier.mod("customization"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCustomizationPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundCustomizationPacket::entityId,
            CustomizationData.STREAM_CODEC, ServerboundCustomizationPacket::data,
            ServerboundCustomizationPacket::new
    );
    public ServerboundCustomizationPacket(int entityId, CustomizationData.Builder data) {
        this(entityId, new CustomizationData(data));
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
