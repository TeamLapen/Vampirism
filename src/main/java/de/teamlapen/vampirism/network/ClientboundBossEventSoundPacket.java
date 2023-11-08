package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

public record ClientboundBossEventSoundPacket(UUID  bossEventUuid, ResourceKey<SoundEvent> sound) implements IMessage.IClientBoundMessage {

    public static final Codec<ClientboundBossEventSoundPacket> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("boss_event_uuid").forGetter(l -> l.bossEventUuid),
                ResourceKey.codec(ForgeRegistries.Keys.SOUND_EVENTS).fieldOf("sound").forGetter(l -> l.sound)
        ).apply(builder, ClientboundBossEventSoundPacket::new);
    });

    static void encode(@NotNull ClientboundBossEventSoundPacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(CODEC, msg);
    }

    static @NotNull ClientboundBossEventSoundPacket decode(@NotNull FriendlyByteBuf buf) {
        return buf.readJsonWithCodec(CODEC);
    }

    public static void handle(final ClientboundBossEventSoundPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.addBossEventSound(msg.bossEventUuid, msg.sound));
        ctx.setPacketHandled(true);
    }
}
