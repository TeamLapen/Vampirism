package de.teamlapen.factions.api.entities.player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import de.teamlapen.factions.api.FactionRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface FactionPlayerBooleanSupplier extends Function<IFactionPlayer<?>, Boolean> {

    Codec<Holder<FactionPlayerBooleanSupplier>> CODEC = Codec.lazyInitialized(() -> FactionRegistries.FACTION_PLAYER_BOOLEAN_SUPPLIER.get().holderByNameCodec());

}
