package de.teamlapen.sync;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleProperty<T> extends Property {

    private final Codec<T> codec;
    private final T defaultValue;
    private final Supplier<T> valueProvider;
    private final Function<T, Boolean> valueSetter;

    public SimpleProperty(ResourceLocation key, boolean sync, Codec<T> codec, T defaultValue, Supplier<T> valueProvider, Function<T, Boolean> valueSetter) {
        super(key, sync);
        this.codec = codec;
        this.defaultValue = defaultValue;
        this.valueProvider = valueProvider;
        this.valueSetter = valueSetter;
    }

    @Override
    public int getStatus() {
        return this.valueProvider.get().hashCode();
    }

    public int storeValue(ValueOutput output, StoreMode mode) {
        T t = this.valueProvider.get();
        output.store(this.key.toString(), this.codec, t);

        return t.hashCode();
    }

    public boolean load(ValueInput input, boolean required) {
        Optional<T> read = input.read(this.key.toString(), this.codec);

        if (read.isPresent()) {
            return this.valueSetter.apply(read.get());
        } else if (required) {
            return this.valueSetter.apply(this.defaultValue);
        }
        return false;
    }
}
