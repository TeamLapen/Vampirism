package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public record ServerboundNameItemPacket(String name) implements CustomPacketPayload {

    public static final Type<ServerboundNameItemPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "name_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundNameItemPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundNameItemPacket::name,
            ServerboundNameItemPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
