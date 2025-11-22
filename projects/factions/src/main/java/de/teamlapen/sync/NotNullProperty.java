package de.teamlapen.sync;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class NotNullProperty<T> extends SimpleProperty<T> {
    public NotNullProperty(ResourceLocation key, boolean sync, Codec<T> codec, T defaultValue, Supplier<T> valueProvider, Function<T, Boolean> valueSetter) {
        super(key, sync, codec, defaultValue, valueProvider, valueSetter);
    }
}