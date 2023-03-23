package de.teamlapen.vampirism.api.entity.player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public record FactionPlayerBooleanSupplier(Function<IFactionPlayer<?>, Boolean> function) implements Function<IFactionPlayer<?>, Boolean> {

    private static final BiMap<ResourceLocation, FactionPlayerBooleanSupplier> REGISTRY = HashBiMap.create();
    public static Codec<FactionPlayerBooleanSupplier> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, s -> REGISTRY.inverse().get(s));

    public static FactionPlayerBooleanSupplier getSupplier(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static ResourceLocation getId(FactionPlayerBooleanSupplier supplier) {
        return REGISTRY.inverse().get(supplier);
    }


    static FactionPlayerBooleanSupplier register(ResourceLocation id, FactionPlayerBooleanSupplier supplier) {
        REGISTRY.put(id, supplier);
        return supplier;
    }

    @Override
    public Boolean apply(IFactionPlayer<?> iFactionPlayer) {
        return this.function.apply(iFactionPlayer);
    }
}
