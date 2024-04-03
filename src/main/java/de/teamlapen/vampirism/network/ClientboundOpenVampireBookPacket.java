package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * open a vampire book on client
 */
public record ClientboundOpenVampireBookPacket(String bookId) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "open_vampire_book");
    public static final Codec<ClientboundOpenVampireBookPacket> CODEC = Codec.STRING.xmap(ClientboundOpenVampireBookPacket::new, p -> p.bookId);

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
