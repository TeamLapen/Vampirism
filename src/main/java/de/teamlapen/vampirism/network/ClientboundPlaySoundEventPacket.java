package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

public record ClientboundPlaySoundEventPacket(Holder<SoundEvent> soundEvent) implements CustomPacketPayload {

    public static final Type<ClientboundPlaySoundEventPacket> TYPE = new Type<>(VResourceLocation.mod("play_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlaySoundEventPacket> CODEC = StreamCodec.composite(SoundEvent.STREAM_CODEC, ClientboundPlaySoundEventPacket::soundEvent, ClientboundPlaySoundEventPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
