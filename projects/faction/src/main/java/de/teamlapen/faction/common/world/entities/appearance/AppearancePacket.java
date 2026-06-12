package de.teamlapen.faction.common.world.entities.appearance;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record AppearancePacket(Map<AppearanceKey<?>, ?> data) {

    private static final Map<AppearanceKey<?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>> streamCodecs = new HashMap<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, AppearancePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AppearancePacket decode(RegistryFriendlyByteBuf buf) {
            Map<AppearanceKey<?>, Object> map = new HashMap<>();
            int size = buf.readVarInt();
            for (int i = 0; i < size; i++) {
                AppearanceKey<?> key = AppearanceKey.STREAM_CODEC.decode(buf);
                //noinspection unchecked
                StreamCodec<? super RegistryFriendlyByteBuf, Object> codec = (StreamCodec<? super RegistryFriendlyByteBuf, Object>) streamCodecs.get(key);
                Object value = codec.decode(buf);
                map.put(key, value);
            }
            return new AppearancePacket(map);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void encode(RegistryFriendlyByteBuf buf, AppearancePacket packet) {
            var filledValues = packet.data.entrySet().stream().map(x -> Triple.of(x.getKey(), x.getValue(), streamCodecs.get(x.getKey()))).toList();
            //noinspection ConstantValue
            buf.writeVarInt((int) filledValues.stream().filter(x -> x.getRight() != null).count());
            for (var entry : filledValues) {
                AppearanceKey.STREAM_CODEC.encode(buf, entry.getLeft());
                ((StreamCodec<? super RegistryFriendlyByteBuf, Object>) entry.getRight()).encode(buf, entry.getMiddle());
            }
        }
    };


    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(AppearanceKey<T> key) {
        return Optional.ofNullable((T) data.get(key));
    }

    public static <T> AppearanceKey<T> register(Identifier key, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        AppearanceKey<T> objectAppearanceKey = new AppearanceKey<>(key);
        streamCodecs.putIfAbsent(objectAppearanceKey, streamCodec);

        return objectAppearanceKey;
    }
}
