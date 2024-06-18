package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.util.ByteBufferCodecUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ClientboundBossEventSoundPacket(UUID  bossEventUuid, ResourceKey<SoundEvent> sound) implements CustomPacketPayload {

    public static final Type<ClientboundBossEventSoundPacket> TYPE = new Type<>(VResourceLocation.mod("boss_event_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBossEventSoundPacket> CODEC = StreamCodec.composite(
            ByteBufferCodecUtil.UUID, ClientboundBossEventSoundPacket::bossEventUuid,
            ResourceKey.streamCodec(Registries.SOUND_EVENT), ClientboundBossEventSoundPacket::sound,
            ClientboundBossEventSoundPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
