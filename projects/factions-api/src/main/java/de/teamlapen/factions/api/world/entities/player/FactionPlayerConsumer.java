package de.teamlapen.factions.api.world.entities.player;

import com.mojang.serialization.Codec;
import de.teamlapen.factions.api.FactionRegistries;

import java.util.function.Consumer;

@FunctionalInterface
public interface FactionPlayerConsumer extends Consumer<IFactionPlayer<?>> {

    Codec<FactionPlayerConsumer> CODEC = Codec.lazyInitialized(() -> FactionRegistries.FACTION_PLAYER_CONSUMER.get().byNameCodec());
}
