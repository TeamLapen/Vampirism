package de.teamlapen.faction.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.world.items.components.IEffectiveRefinementSet;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record EffectiveRefinementSet(@Nullable IRefinementSet set) implements IEffectiveRefinementSet {

    public static final EffectiveRefinementSet EMPTY = new EffectiveRefinementSet(null);
    public static final Codec<EffectiveRefinementSet> CODEC = ExtraCodecs.optionalEmptyMap(ModRegistries.REFINEMENT_SETS.byNameCodec()).xmap(x -> new EffectiveRefinementSet(x.orElse(null)), x -> Optional.ofNullable(x.set()));
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectiveRefinementSet> STREAM_CODEC = ByteBufCodecs.optional(ByteBufCodecs.registry(FactionRegistries.Keys.REFINEMENT_SET)).map(x -> new EffectiveRefinementSet(x.orElse(null)), x -> Optional.ofNullable(x.set()));
}
