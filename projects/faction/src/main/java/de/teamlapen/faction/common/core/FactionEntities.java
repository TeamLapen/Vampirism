package de.teamlapen.faction.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.event.PlayerEventHandlerEvent;
import de.teamlapen.faction.common.advancements.criterion.FactionSubPredicate;
import de.teamlapen.faction.common.advancements.criterion.PlayerFactionSubPredicate;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
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
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATES = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<UUID>>> OPTIONAL_UUID = DATA_SERIALIZER.register("optional_uuid", () -> (EntityDataSerializer.ForValueType<Optional<UUID>>) (() -> ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC)));

    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<PlayerFactionSubPredicate>> PLAYER_FACTION_SUB_PREDICATE = ENTITY_SUB_PREDICATES.register("player_faction", () -> PlayerFactionSubPredicate.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<FactionSubPredicate>> FACTION_SUB_PREDICATE = ENTITY_SUB_PREDICATES.register("faction", () -> FactionSubPredicate.CODEC);

    static void register(IEventBus bus) {
        DATA_SERIALIZER.register(bus);
        ENTITY_SUB_PREDICATES.register(bus);
    }

    static void registerPlayerEventHandler(PlayerEventHandlerEvent event) {
        event.addAttachmentListener(FactionAttachments.FACTION_PLAYER_HANDLER);
        event.addAttachmentListener(FactionAttachments.REFINEMENT_HANDLER);
        event.addServerAttachmentListener(FactionAttachments.TASK_MANAGER);
    }
}
