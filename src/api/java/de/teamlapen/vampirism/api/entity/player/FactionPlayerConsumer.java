package de.teamlapen.vampirism.api.entity.player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public record FactionPlayerConsumer(Consumer<IFactionPlayer<?>> consumer) implements Consumer<IFactionPlayer<?>> {

    private static final BiMap<ResourceLocation, FactionPlayerConsumer> REGISTRY = HashBiMap.create();
    public static Codec<FactionPlayerConsumer> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, s -> REGISTRY.inverse().get(s));

    public static FactionPlayerConsumer getSupplier(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static ResourceLocation getId(FactionPlayerConsumer supplier) {
        return REGISTRY.inverse().get(supplier);
    }


    static FactionPlayerConsumer register(ResourceLocation id, FactionPlayerConsumer supplier) {
        REGISTRY.put(id, supplier);
        return supplier;
    }

    @Override
    public void accept(IFactionPlayer<?> iFactionPlayer) {
        this.consumer.accept(iFactionPlayer);
    }
}
