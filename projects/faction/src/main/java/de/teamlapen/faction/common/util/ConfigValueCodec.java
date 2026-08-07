package de.teamlapen.faction.common.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.misc.extensions.IConfigValue;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ConfigValueCodec {

    private static final BiMap<Identifier, ModConfigSpec> specs = Maps.synchronizedBiMap(HashBiMap.create());

    public static void register(Identifier id, ModConfigSpec spec) {
        specs.put(id, spec);
    }

    private static final Codec<ModConfigSpec> typeCodec = Identifier.CODEC.xmap(specs::get, x -> specs.inverse().get(x));

    private record ConfigValueKey(ModConfigSpec type, List<String> path) {
        static final Codec<ConfigValueKey> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                typeCodec.fieldOf("type").forGetter(ConfigValueKey::type),
                Codec.STRING.listOf().fieldOf("path").forGetter(ConfigValueKey::path)
        ).apply(inst, ConfigValueKey::new));
    }

    private static final Codec<ModConfigSpec.ConfigValue<?>> CODEC = ConfigValueKey.CODEC.flatXmap(
            key -> {
                ModConfigSpec spec = key.type();
                ModConfigSpec.ConfigValue<?> value = spec.getValues().get(key.path());
                if (value == null) {
                    return DataResult.error(() -> "No config value found at path " + key.path() + " for " + key.type());
                }
                return DataResult.success(value);
            },
            value -> DataResult.success(new ConfigValueKey(((IConfigValue) value).factionapi$spec(), value.getPath()))
    );

    @SuppressWarnings("unchecked")
    public static <TValue> Codec<ModConfigSpec.ConfigValue<TValue>> codec() {
        return (Codec<ModConfigSpec.ConfigValue<TValue>>) (Object) CODEC;
    }
}
