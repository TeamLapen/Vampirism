package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Syncs the collapse progress of the Velmorra dimension (0 = just started, 1 = destruction) so the client can render
 * the environmental breakdown (fog, color, rumble, particles).
 */
public record ClientboundVelmorraCollapsePacket(float progress) implements CustomPacketPayload {

    public static final Type<ClientboundVelmorraCollapsePacket> TYPE = new Type<>(VIdentifier.mod("velmorra_collapse"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundVelmorraCollapsePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ClientboundVelmorraCollapsePacket::progress,
            ClientboundVelmorraCollapsePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
