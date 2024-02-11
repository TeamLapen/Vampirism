package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.UUID;

public record ClientboundBossEventSoundPacket(UUID  bossEventUuid, ResourceKey<SoundEvent> sound) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "boss_event_sound");
    public static final Codec<ClientboundBossEventSoundPacket> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("boss_event_uuid").forGetter(l -> l.bossEventUuid),
                    ResourceKey.codec(Registries.SOUND_EVENT).fieldOf("sound").forGetter(l -> l.sound)
            ).apply(builder, ClientboundBossEventSoundPacket::new));

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
