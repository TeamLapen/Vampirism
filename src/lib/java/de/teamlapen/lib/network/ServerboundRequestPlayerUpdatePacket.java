package de.teamlapen.lib.network;

import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Request an update packet for the players {@link de.teamlapen.lib.lib.storage.IAttachedSyncable} (e.g. on World join)
 */
public class ServerboundRequestPlayerUpdatePacket implements CustomPacketPayload {
    public static final ServerboundRequestPlayerUpdatePacket INSTANCE = new ServerboundRequestPlayerUpdatePacket();
    public static final Type<ServerboundRequestPlayerUpdatePacket> TYPE = new Type<>(new ResourceLocation(LIBREFERENCE.MODID, "request_player_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestPlayerUpdatePacket> CODEC = StreamCodec.unit(INSTANCE);

    private ServerboundRequestPlayerUpdatePacket() {

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
