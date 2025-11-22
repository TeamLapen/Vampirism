package de.teamlapen.sync;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import de.teamlapen.factions.common.util.ModCodecs;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class NullableProperty<T> extends SimpleProperty<Optional<T>> {
    public NullableProperty(ResourceLocation key, boolean sync, Codec<@Nullable T> codec, @Nullable T defaultValue, Supplier<@Nullable T> valueProvider, Function<@Nullable T, Boolean> valueSetter) {
        super(key, sync, Codec.optionalField("value", codec, true).codec(), Optional.ofNullable(defaultValue), () -> Optional.ofNullable(valueProvider.get()), value -> valueSetter.apply(value.orElse(null)));
    }
}
