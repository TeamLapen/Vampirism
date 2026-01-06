package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import de.teamlapen.vampirism.common.world.items.component.VampireBook;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * open a vampire book on client
 */
public record ClientboundOpenVampireBookPacket(IVampireBook vampireBook) implements CustomPacketPayload {

    public static final Type<ClientboundOpenVampireBookPacket> TYPE = new Type<>(VIdentifier.mod("open_vampire_book"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenVampireBookPacket> CODEC = StreamCodec.composite(VampireBook.STREAM_CODEC, ClientboundOpenVampireBookPacket::vampireBook, ClientboundOpenVampireBookPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
