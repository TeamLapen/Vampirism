package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.FactionDataComponents;
import de.teamlapen.faction.api.world.entities.ICustomizationOption;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MinionAppearance<T> implements ICustomizationOption<T> {
    public static final MinionAppearance<String> NAME_TYPE = new MinionAppearance<>(FactionDataComponents.MINION_NAME, "Minion") {
        @Nullable
        @Override
        public String setValue(IMinionEntity entity, IMinionData data, String value) {
            var old = super.setValue(entity, data, value);
            entity.asEntity().setCustomName(Component.literal(value));
            return old;
        }
    };
    public static final MinionAppearance<Integer> SKIN_TYPE = new MinionAppearance<>(FactionDataComponents.MINION_SKIN, 0);
    public static final MinionAppearance<Boolean> LORD_SKIN = new MinionAppearance<>(FactionDataComponents.MINION_USE_LORD_SKIN, false);
    public static final MinionAppearance<Boolean> MINION_SKIN = new MinionAppearance<>(FactionDataComponents.MINION_USE_MINION_SKIN, false);

    private final Holder<DataComponentType<T>> type;
    private final T defaultValue;

    public MinionAppearance(Holder<DataComponentType<T>> type, T defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public MinionAppearance(DeferredHolder<DataComponentType<?>, DataComponentType<T>> type, T defaultValue) {
        this((Holder)type, defaultValue);
    }

    public T currentValue(IMinionEntity entity, IMinionData data) {
        return data.getOrDefault(type::value, defaultValue);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public T currentValue(IMinionEntity entity, Optional<? extends IMinionData> data) {
        return data.map(x -> x.get(type::value)).orElse(defaultValue);
    }

    @Nullable
    public T setValue(IMinionEntity entity, IMinionData data, T value) {
        return data.set(type::value, value);
    }

    @Override
    public Identifier id() {
        return type.unwrapKey().orElseThrow().identifier();
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, T> codec() {
        return type.value().streamCodec();
    }
}
