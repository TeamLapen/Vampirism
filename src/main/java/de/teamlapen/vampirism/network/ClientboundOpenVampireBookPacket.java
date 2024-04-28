package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * open a vampire book on client
 */
public record ClientboundOpenVampireBookPacket(String bookId) implements CustomPacketPayload {

    public static final Type<ClientboundOpenVampireBookPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "open_vampire_book"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenVampireBookPacket> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ClientboundOpenVampireBookPacket::bookId, ClientboundOpenVampireBookPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
