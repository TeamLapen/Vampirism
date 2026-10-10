package de.teamlapen.faction.common.world.entities.customization;

import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.ICustomizationOption;
import de.teamlapen.faction.api.world.entities.minion.MinionAppearance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public record CustomizationData(Map<Identifier, Object> data) {

    private static final Map<Identifier, ICustomizationOption<?>> options = new HashMap<>();

    public static void register(ICustomizationOption<?> option) {
        options.put(option.id(), option);
    }

    @Nullable
    public static ICustomizationOption<?> get(Identifier id) {
        return options.get(id);
    }

    static {
        register(MinionAppearance.NAME_TYPE);
        register(MinionAppearance.SKIN_TYPE);
        register(MinionAppearance.MINION_SKIN);
        register(MinionAppearance.LORD_SKIN);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomizationData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CustomizationData decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            Map<Identifier, Object> data = new HashMap<>();
            int count = registryFriendlyByteBuf.readVarInt();
            for (int i = 0; i < count; i++) {
                Identifier identifier = registryFriendlyByteBuf.readIdentifier();
                ICustomizationOption<?> iCustomizationOption = options.get(identifier);
                Object decode = iCustomizationOption.codec().decode(registryFriendlyByteBuf);
                data.put(identifier, decode);
            }
            return new CustomizationData(data);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf o, CustomizationData customizationData) {
            o.writeVarInt(customizationData.data.size());
            for (Map.Entry<Identifier, ?> entry : customizationData.data.entrySet()) {
                var option = options.get(entry.getKey());
                o.writeIdentifier(entry.getKey());
                option.codec().encode(o, SafeCast.cast(entry.getValue()));
            }
        }
    };

    public CustomizationData(Builder builder) {
        this(builder.data);
    }

    public static <T> Builder with(ICustomizationOption<T> option, T value) {
        return new Builder().with(option, value);
    }

    public static class Builder {
        private Map<Identifier, Object> data = new HashMap<>();

        public <T> Builder with(ICustomizationOption<T> option, T value) {
            data.put(option.id(), value);
            return this;
        }
    }
}
