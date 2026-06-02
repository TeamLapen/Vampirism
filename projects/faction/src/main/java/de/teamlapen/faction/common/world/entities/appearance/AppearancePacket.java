package de.teamlapen.faction.common.world.entities.appearance;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record AppearancePacket(Map<AppearanceKey<?>, ?> data) {

    private static final Map<AppearanceKey<?>, Codec<?>> codecs = new HashMap<>();
    private static final Map<AppearanceKey<?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>> streamCodecs = new HashMap<>();

    public static final Codec<AppearancePacket> CODEC = AppearanceKey.CODEC.dispatch(
            "key",
            Entry::key,
            key -> ((Codec<Object>) codecs.get(key)).fieldOf("value").xmap(v -> new Entry<>((AppearanceKey<Object>) key, v), Entry::value)
    ).listOf().xmap(
            entries -> {
                Map<AppearanceKey<?>, Object> map = new HashMap<>();
                for (Entry<?> entry : entries) {
                    map.put(entry.key(), entry.value());
                }
                return new AppearancePacket(map);
            },
            packet -> packet.data().entrySet().stream()
                    .map(e -> new Entry<>((AppearanceKey<Object>) e.getKey(), e.getValue()))
                    .toList()
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AppearancePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AppearancePacket decode(RegistryFriendlyByteBuf buf) {
            Map<AppearanceKey<?>, Object> map = new HashMap<>();
            int size = buf.readVarInt();
            for (int i = 0; i < size; i++) {
                AppearanceKey<?> key = new AppearanceKey<>(net.minecraft.resources.Identifier.STREAM_CODEC.decode(buf));
                StreamCodec<? super RegistryFriendlyByteBuf, Object> codec = (StreamCodec<? super RegistryFriendlyByteBuf, Object>) streamCodecs.get(key);
                Object value = codec.decode(buf);
                map.put(key, value);
            }
            return new AppearancePacket(map);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, AppearancePacket packet) {
            buf.writeVarInt(packet.data().size());
            for (Map.Entry<AppearanceKey<?>, ?> entry : packet.data().entrySet()) {
                AppearanceKey<?> key = entry.getKey();
                net.minecraft.resources.Identifier.STREAM_CODEC.encode(buf, key.Id());
                StreamCodec<? super RegistryFriendlyByteBuf, Object> codec = (StreamCodec<? super RegistryFriendlyByteBuf, Object>) streamCodecs.get(key);
                codec.encode(buf, entry.getValue());
            }
        }
    };


    public <T> Optional<T> get(AppearanceKey<T> key) {
        return Optional.ofNullable((T) data.get(key));
    }

    public static <T> void registerCodec(AppearanceKey<T> key, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        codecs.putIfAbsent(key, codec);
        streamCodecs.putIfAbsent(key, streamCodec);
    }

    private record Entry<T>(AppearanceKey<T> key, T value) {}

}
