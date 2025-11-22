package de.teamlapen.sync;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListProperty<T> extends Property {

    private final Codec<T> codec;
    private final Supplier<List<T>> defaultValue;
    private final Supplier<List<T>> valueProvider;
    private final Function<List<T>, Boolean> valueSetter;

    public ListProperty(ResourceLocation key, boolean sync, Codec<T> codec, Supplier<List<T>> defaultValue, Supplier<List<T>> valueProvider, Function<List<T>, Boolean> valueSetter) {
        super(key, sync);
        this.codec = codec;
        this.defaultValue = defaultValue;
        this.valueProvider = valueProvider;
        this.valueSetter = valueSetter;
    }


    @Override
    public int getStatus() {
        return Objects.hash(this.valueProvider.get());
    }

    @Override
    public int storeValue(ValueOutput output, StoreMode mode) {
        List<T> valueList = valueProvider.get();
        var outputList = output.list(key.toString(), codec);
        valueList.forEach(outputList::add);
        return valueList.hashCode();
    }

    @Override
    public boolean load(ValueInput input, boolean required) {
        Optional<ValueInput.TypedInputList<T>> list = input.list(key.toString(), codec);
        if (list.isPresent()) {
            return valueSetter.apply(list.get().stream().collect(Collectors.toList()));
        } else if (required) {
            return valueSetter.apply(defaultValue.get());
        }

        return false;
    }
}
