package de.teamlapen.vampirism.network.packet.garlic;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundAddGarlicEmitterPacket(GarlicLevel.Emitter emitter) implements CustomPacketPayload {
    public static final Type<ClientboundAddGarlicEmitterPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "add_garlic_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddGarlicEmitterPacket> CODEC = StreamCodec.composite(
            GarlicLevel.Emitter.STREAM_CODEC, ClientboundAddGarlicEmitterPacket::emitter,
            ClientboundAddGarlicEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
