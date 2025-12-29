package de.teamlapen.factions.api.world.entities.player;

import com.mojang.serialization.Codec;
import de.teamlapen.factions.api.FactionRegistries;
import net.minecraft.core.Holder;

import java.util.function.Function;

public interface FactionPlayerBooleanSupplier extends Function<IFactionPlayer<?>, Boolean> {

    Codec<Holder<FactionPlayerBooleanSupplier>> CODEC = Codec.lazyInitialized(() -> FactionRegistries.FACTION_PLAYER_BOOLEAN_SUPPLIER.get().holderByNameCodec());

}
