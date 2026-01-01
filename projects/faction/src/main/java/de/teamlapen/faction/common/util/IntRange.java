package de.teamlapen.faction.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record IntRange(@Nullable Integer min, @Nullable Integer max) {

    public static final Codec<IntRange> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("min").forGetter(x -> Optional.ofNullable(x.min)),
            Codec.INT.optionalFieldOf("max").forGetter(x -> Optional.ofNullable(x.max))
    ).apply(inst, IntRange::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IntRange> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.INT), x -> Optional.ofNullable(x.min),
            ByteBufCodecs.optional(ByteBufCodecs.INT), x -> Optional.ofNullable(x.max),
            IntRange::new
    );

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private IntRange(Optional<Integer> min, Optional<Integer> max) {
        this(min.orElse(null), max.orElse(null));
    }

    public static IntRange withBounds(int min, int max) {
        return new IntRange(min, max);
    }

    public static IntRange exact(int value) {
        return new IntRange(value, value);
    }

    public static IntRange lowerBound(int min) {
        return new IntRange(min, null);
    }

    public static IntRange upperBound(int max) {
        return new IntRange(null, max);
    }

    public boolean contains(int value) {
        if (this.min != null && value < this.min) {
            return false;
        }
        return this.max == null || value <= this.max;
    }
}
