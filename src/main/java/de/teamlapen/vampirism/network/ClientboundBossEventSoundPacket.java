package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.util.ByteBufferCodecUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ClientboundBossEventSoundPacket(UUID  bossEventUuid, ResourceKey<SoundEvent> sound) implements CustomPacketPayload {

    public static final Type<ClientboundBossEventSoundPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "boss_event_sound"));
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
