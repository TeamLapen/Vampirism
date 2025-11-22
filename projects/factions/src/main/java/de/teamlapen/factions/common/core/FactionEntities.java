package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.util.REFERENCE;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.UUID;

public class FactionEntities {

    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, REFERENCE.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<UUID>>> OPTIONAL_UUID = DATA_SERIALIZER.register("optional_uuid", () -> (EntityDataSerializer.ForValueType<Optional<UUID>>) (() -> ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC)));

    static void register(IEventBus bus) {
        DATA_SERIALIZER.register(bus);
    }
}
