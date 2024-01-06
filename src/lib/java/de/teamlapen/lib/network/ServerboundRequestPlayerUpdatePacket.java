package de.teamlapen.lib.network;

import com.mojang.serialization.Codec;
import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Request an update packet for the players {@link de.teamlapen.lib.lib.network.ISyncable.ISyncableAttachment} (e.g. on World join)
 */
public class ServerboundRequestPlayerUpdatePacket implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(LIBREFERENCE.MODID, "request_player_update");
    public static final Codec<ServerboundRequestPlayerUpdatePacket> CODEC = Codec.unit(ServerboundRequestPlayerUpdatePacket::new);

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
