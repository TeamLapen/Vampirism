package de.teamlapen.faction.common.world.entities.appearance;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;

@SuppressWarnings("unused")
public record AppearanceKey<T>(Identifier Id) {

    public static final Codec<AppearanceKey<?>> CODEC = Identifier.CODEC.xmap(AppearanceKey::new, AppearanceKey::Id);
    public static final StreamCodec<ByteBuf, AppearanceKey<?>> STREAM_CODEC = Identifier.STREAM_CODEC.map(AppearanceKey::new, AppearanceKey::Id);

    public static class AppearanceMap extends HashMap<AppearanceKey<?>, Object> {

        public <T> void set(AppearanceKey<T> key, T value) {
            put(key, value);
        }
    }
}
